package com.hr.newwork.services;

import com.hr.newwork.repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for handling logout and refresh token invalidation.
 */
@Service
@RequiredArgsConstructor
public class LogoutService {
    private final RefreshTokenRepository refreshTokenRepository;
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
}
