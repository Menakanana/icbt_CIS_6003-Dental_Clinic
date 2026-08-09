package com.dentalclinic.service;

import com.dentalclinic.dto.AuthResponseDTO;
import com.dentalclinic.dto.LoginRequestDTO;
import com.dentalclinic.entity.User;
import com.dentalclinic.exception.BadCredentialsException;
import com.dentalclinic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service handling user authentication and credential validation with Debug Logging.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponseDTO authenticate(LoginRequestDTO loginRequest) {
        String username = loginRequest.getUsername();
        String rawPassword = loginRequest.getPassword();

        System.out.println("-------------------------------------------------");
        System.out.println("[DEBUG-AUTH] Login Attempt Received!");
        System.out.println("[DEBUG-AUTH] Username: '" + username + "'");
        System.out.println("[DEBUG-AUTH] Raw Password: '" + rawPassword + "'");

        // Step 1: Query user from database by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println("[DEBUG-AUTH] FAILED: User '" + username + "' not found in DB!");
                    return new BadCredentialsException("Invalid username or password");
                });

        System.out.println("[DEBUG-AUTH] User Found in DB: ID=" + user.getUserId() + ", Role=" + user.getRole() + ", Active=" + user.getIsActive());
        System.out.println("[DEBUG-AUTH] DB Hashed Password: " + user.getPassword());

        // Step 2: Verify if the user account is active
        if (Boolean.FALSE.equals(user.getIsActive())) {
            System.out.println("[DEBUG-AUTH] FAILED: User account is disabled!");
            throw new BadCredentialsException("Account is disabled. Please contact administrator.");
        }

        // Step 3: Verify plain-text password against BCrypt hashed password in DB
        boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());
        System.out.println("[DEBUG-AUTH] Password Match Result: " + matches);

        if (!matches) {
            System.out.println("[DEBUG-AUTH] FAILED: Password mismatch for user '" + username + "'!");
            throw new BadCredentialsException("Invalid username or password");
        }

        System.out.println("[DEBUG-AUTH] SUCCESS: Authentication passed for user '" + username + "'!");
        System.out.println("-------------------------------------------------");

        // Step 4: Update the last login timestamp for audit tracking
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Step 5: Generate session/bearer token and construct response DTO
        String generatedToken = "jwt-bearer-" + UUID.randomUUID().toString();
        return new AuthResponseDTO(
                generatedToken,
                user.getUserId(),
                user.getUsername(),
                user.getFullName(),
                user.getRole()
        );
    }
}
