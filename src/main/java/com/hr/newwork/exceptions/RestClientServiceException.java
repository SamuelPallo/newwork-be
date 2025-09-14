package com.hr.newwork.exceptions;

/**
 * Custom exception for RestClientService errors.
 */
public class RestClientServiceException extends RuntimeException {
    public RestClientServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

