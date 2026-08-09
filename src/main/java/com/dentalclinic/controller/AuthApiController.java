package com.dentalclinic.controller;

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
 * Layer: Presentation / Controller Layer
 * Base URL: /api/auth
 * Handles: JSON authentication requests and returns auth token / user details.
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
}
