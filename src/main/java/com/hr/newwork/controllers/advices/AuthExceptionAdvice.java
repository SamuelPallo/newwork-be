package com.hr.newwork.controllers.advices;

import com.hr.newwork.controllers.AuthController;
import com.hr.newwork.exceptions.LoginFailedException;
import com.hr.newwork.exceptions.RefreshTokenFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
/**
 * Handles authentication-related exceptions and returns meaningful error responses.
 */
@Slf4j
@RestControllerAdvice(assignableTypes = {AuthController.class})
public class AuthExceptionAdvice {

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<Map<String, String>> handleLoginFailed(LoginFailedException ex) {
        log.warn("LoginFailedException handled: {}", ex.getMessage());
        Map<String, String> body = new HashMap<>();
        body.put("error", "Login failed");
        body.put("message", "Invalid username or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(RefreshTokenFailedException.class)
    public ResponseEntity<Map<String, String>> handleRefreshFailed(RefreshTokenFailedException ex) {
        log.warn("RefreshTokenFailedException handled: {}", ex.getMessage());
        Map<String, String> body = new HashMap<>();
        body.put("error", "Refresh token failed");
        body.put("message", "Invalid or expired refresh token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        log.error("Unhandled exception in AuthController: ", ex);
        Map<String, String> body = new HashMap<>();
        body.put("error", "Internal server error");
        body.put("message", ex.getMessage() != null ? ex.getMessage() : "Unexpected error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
