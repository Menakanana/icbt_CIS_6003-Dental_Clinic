package com.dentalclinic.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler for REST API Controllers.
 * 
 * Layer: Exception Handling / Controller Advice
 * Role: Intercepts exceptions thrown anywhere in API controllers or services and returns clean, standard JSON error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles authentication failures (bad username or password).
     * 
     * @return 401 Unauthorized JSON response
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("error", "Unauthorized");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    /**
     * Handles missing entity lookups.
     * 
     * @return 404 Not Found JSON response
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Handles validation errors when DTO fields fail @NotNull, @NotBlank, etc.
     * 
     * @return 400 Bad Request JSON response with specific field error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            fieldErrors.put(error.getField(), error.getDefaultMessage())
        );
        body.put("fieldErrors", fieldErrors);
        body.put("message", "Validation failed for request payload");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Handles parameter type mismatch errors (e.g. invalid date format or non-numeric ID).
     * 
     * @return 400 Bad Request JSON response
     */
    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", String.format("Failed to convert parameter '%s' with value '%s'. Supported date formats: yyyy-MM-dd, dd-MM-yyyy, MM-dd-yyyy.", ex.getName(), ex.getValue()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Handles database constraint violations and data integrity exceptions cleanly without exposing raw SQL schemas.
     */
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(org.springframework.dao.DataIntegrityViolationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "Data Conflict");
        body.put("message", sanitizeErrorMessage(ex));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Fallback handler for unhandled generic server exceptions.
     * 
     * @return 500 Internal Server Error JSON response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", sanitizeErrorMessage(ex));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private String sanitizeErrorMessage(Throwable ex) {
        if (ex == null) return "An unexpected system error occurred.";
        
        String msg = ex.getMessage();
        if (msg == null || msg.trim().isEmpty()) {
            return "An unexpected system error occurred. Please try again.";
        }
        
        String lower = msg.toLowerCase();
        
        if (lower.contains("sql") || lower.contains("hibernate") || lower.contains("could not execute statement") ||
            lower.contains("insert into") || lower.contains("update ") || lower.contains("delete from") ||
            lower.contains("column") || lower.contains("table") || lower.contains("truncat")) {
            
            if (lower.contains("duplicate") || lower.contains("unique") || lower.contains("primary key")) {
                return "A record with the same unique identifier (e.g. NIC or Username) already exists in the system.";
            }
            if (lower.contains("truncat") || lower.contains("binary data")) {
                return "One or more input values exceed the allowed character length limit. Please check your entries and try again.";
            }
            if (lower.contains("foreign key") || lower.contains("fk_")) {
                return "Unable to complete request because related records exist or the selected item is invalid.";
            }
            return "Database operation failed. Please verify your input entries and try again.";
        }
        
        return msg;
    }
}
