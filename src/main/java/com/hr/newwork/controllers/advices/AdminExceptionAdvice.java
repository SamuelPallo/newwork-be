package com.hr.newwork.controllers.advices;

import com.hr.newwork.controllers.AdminController;
import com.hr.newwork.exceptions.ForbiddenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller advice for handling exceptions thrown by admin endpoints.
 * Maps exceptions to appropriate HTTP responses for the AdminController.
 */
@RestControllerAdvice(assignableTypes = {AdminController.class})
public class AdminExceptionAdvice {
    /**
     * Handles forbidden access attempts to admin endpoints.
     *
     * @param ex the ForbiddenException thrown
     * @return HTTP 403 response with error details
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, String>> handleForbidden(ForbiddenException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Forbidden");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    /**
     * Handles all other exceptions thrown by admin endpoints.
     *
     * @param ex the Exception thrown
     * @return HTTP 500 response with error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleOther(Exception ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Internal server error");
        body.put("message", ex.getMessage() != null ? ex.getMessage() : "Unexpected error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
