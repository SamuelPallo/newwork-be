package com.hr.newwork.exceptions;

/**
 * Exception thrown when login fails due to invalid credentials or other reasons.
 */
public class LoginFailedException extends RuntimeException {
    public LoginFailedException(String message) {
        super(message);
    }
}

