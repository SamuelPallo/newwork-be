package com.hr.newwork.services;

import com.hr.newwork.repositories.RefreshTokenRepository;
import com.hr.newwork.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.hr.newwork.data.entity.User;

/**
 * Service for handling logout and refresh token invalidation.
 */
@Service
@RequiredArgsConstructor
public class LogoutService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    /**
     * Invalidates the given refresh token.
     * @param refreshToken the refresh token to invalidate
     */
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken).ifPresent(token -> {
            token.setValid(false);
            refreshTokenRepository.save(token);
        });
    }

    /**
     * Invalidates all valid and unexpired refresh tokens for the current authenticated user.
     */
    public void logoutForCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDetails)) {
            return; // No authenticated user
        }
        String email = ((UserDetails) auth.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return;
        java.util.Date now = new java.util.Date();
        refreshTokenRepository.findAllByUserIdAndValidTrueAndExpiryDateAfter(user.getId(), now)
                .forEach(token -> {
                    token.setValid(false);
                    refreshTokenRepository.save(token);
                });
    }
}
