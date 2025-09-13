package com.hr.newwork.exceptions;

/**
 * Exception thrown when refresh token validation fails or is expired.
 */
public class RefreshTokenFailedException extends RuntimeException {
    public RefreshTokenFailedException(String message) {
        super(message);
    }
}

