package com.hr.newwork.exceptions;

/**
 * Exception thrown when an action is forbidden for the current user.
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}

