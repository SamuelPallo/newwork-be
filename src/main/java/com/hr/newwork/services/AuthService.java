package com.hr.newwork.services;

import com.hr.newwork.config.security.JwtTokenProvider;
import com.hr.newwork.data.dto.LoginRequest;
import com.hr.newwork.data.dto.LoginResponse;
import com.hr.newwork.data.dto.RefreshTokenRequest;
import com.hr.newwork.data.entity.RefreshToken;
import com.hr.newwork.data.entity.User;
import com.hr.newwork.exceptions.RefreshTokenFailedException;
import com.hr.newwork.repositories.RefreshTokenRepository;
import com.hr.newwork.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Authenticates a user using email and password, and returns JWT tokens.
     * Persists a refresh token for later validation.
     * @param loginRequest the login request containing email and password
     * @return LoginResponse containing accessToken, refreshToken, and expiresIn
     * @throws RuntimeException if credentials are invalid
     */
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // At this point, authentication succeeded, so the user is guaranteed to exist
        User user = userRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new IllegalStateException("User should exist after successful authentication"));

        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshTokenStr = UUID.randomUUID().toString();
        Date expiry = new Date(System.currentTimeMillis() + 7 * 24 * 3600 * 1000); // 7 days
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenStr);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(expiry);
        refreshToken.setValid(true);
        refreshTokenRepository.save(refreshToken);

        long expiresIn = new Date(System.currentTimeMillis() + 3600000).getTime();
        return new LoginResponse(accessToken, refreshTokenStr, expiresIn);
    }

    /**
     * Issues a new access token using a valid refresh token.
     * @param refreshTokenRequest the refresh token request
     * @return LoginResponse containing new accessToken, original refreshToken, and expiresIn
     * @throws RuntimeException if refresh token is invalid or expired
     */
    @Transactional
    public LoginResponse refresh(RefreshTokenRequest refreshTokenRequest) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenRequest.getRefreshToken())
                .orElseThrow(() -> new RefreshTokenFailedException("Invalid refresh token"));
        if (!refreshToken.isValid() || refreshToken.getExpiryDate().before(new Date())) {
            throw new RefreshTokenFailedException("Refresh token expired or invalid");
        }
        User user = refreshToken.getUser();
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, null);
        String accessToken = jwtTokenProvider.generateToken(authentication);
        long expiresIn = new Date(System.currentTimeMillis() + 3600000).getTime();
        return new LoginResponse(accessToken, refreshToken.getToken(), expiresIn);
    }
}
