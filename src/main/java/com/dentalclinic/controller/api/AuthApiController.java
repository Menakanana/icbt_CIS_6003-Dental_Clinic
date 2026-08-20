package com.dentalclinic.controller.api;

import com.dentalclinic.dto.AuthResponseDTO;
import com.dentalclinic.dto.LoginRequestDTO;
import com.dentalclinic.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Authentication API endpoints.
 * 
 * Layer: Presentation / API Controller Layer
 * Base URL: /api/auth
 * Handles: JSON authentication requests using shared AuthService logic.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthApiController {

    private final AuthService authService;

    @Autowired
    public AuthApiController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Handles User Login API request.
     * 
     * Endpoint: POST /api/auth/login
     * Request Body: LoginRequestDTO (username, password)
     * Response: 200 OK with AuthResponseDTO if valid; 401 Unauthorized if invalid.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        AuthResponseDTO response = authService.authenticate(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Handles Forgot Password / Password Reset via Email request.
     * 
     * Endpoint: POST /api/auth/forgot-password
     * Request Body: { "email": "user@example.com" }
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<java.util.Map<String, String>> forgotPassword(@RequestBody java.util.Map<String, String> request) {
        String emailOrUsername = request.get("email");
        if (emailOrUsername == null || emailOrUsername.trim().isEmpty()) {
            emailOrUsername = request.get("username");
        }
        String message = authService.processForgotPassword(emailOrUsername);
        return ResponseEntity.ok(java.util.Map.of("message", message));
    }

    /**
     * Handles Reset Password execution request.
     * 
     * Endpoint: POST /api/auth/reset-password
     * Request Body: { "token": "RST-XXXXXX", "newPassword": "myNewPassword" }
     */
    @PostMapping("/reset-password")
    public ResponseEntity<java.util.Map<String, Object>> resetPassword(@RequestBody java.util.Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        try {
            boolean success = authService.resetPassword(token, newPassword);
            return ResponseEntity.ok(java.util.Map.of("success", true, "message", "Your password has been reset successfully. You can now sign in."));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(java.util.Map.of("success", false, "message", ex.getMessage()));
        }
    }
}
