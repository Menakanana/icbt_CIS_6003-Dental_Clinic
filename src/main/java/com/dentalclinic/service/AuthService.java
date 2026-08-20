package com.dentalclinic.service;

import com.dentalclinic.dto.AuthResponseDTO;
import com.dentalclinic.dto.LoginRequestDTO;
import com.dentalclinic.entity.PasswordResetToken;
import com.dentalclinic.entity.User;
import com.dentalclinic.exception.BadCredentialsException;
import com.dentalclinic.repository.PasswordResetTokenRepository;
import com.dentalclinic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service handling user authentication and credential validation with Debug Logging.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailNotificationService emailNotificationService;
    private final PasswordResetTokenRepository resetTokenRepository;

    @Autowired
    public AuthService(UserRepository userRepository, 
                       PasswordEncoder passwordEncoder, 
                       EmailNotificationService emailNotificationService,
                       PasswordResetTokenRepository resetTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailNotificationService = emailNotificationService;
        this.resetTokenRepository = resetTokenRepository;
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

    @Transactional
    public String processForgotPassword(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter a valid email address or username.");
        }

        String searchKey = input.trim();
        System.out.println("[DEBUG-AUTH] Processing Password Reset Request for: '" + searchKey + "'");

        User user = userRepository.findByUsername(searchKey)
                .orElseGet(() -> userRepository.findByEmail(searchKey).orElse(null));

        if (user != null) {
            // Delete any existing active reset tokens for this user
            resetTokenRepository.deleteByUser(user);

            // Generate a secure reset token
            String resetToken = "RST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(30);

            // Save token entity to password_reset_tokens table
            PasswordResetToken tokenEntity = new PasswordResetToken(resetToken, user, expiryDate);
            resetTokenRepository.save(tokenEntity);
            System.out.println("[DEBUG-AUTH] Persisted Reset Token '" + resetToken + "' in DB for user ID: " + user.getUserId());

            String emailToUse = (user.getEmail() != null && !user.getEmail().isEmpty()) 
                    ? user.getEmail() 
                    : (searchKey.contains("@") ? searchKey : user.getUsername() + "@sunrisedental.com");

            System.out.println("[DEBUG-AUTH] Sending password reset email to: " + emailToUse);
            emailNotificationService.sendPasswordResetEmail(emailToUse, user.getFullName(), resetToken);
        } else {
            System.out.println("[DEBUG-AUTH] User not found for '" + searchKey + "'. Still returning standard security message.");
        }

        return "If an account matching '" + searchKey + "' exists, a password reset link has been dispatched to your email.";
    }

    public boolean validatePasswordResetToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        return resetTokenRepository.findByToken(token.trim())
                .map(t -> !t.getIsUsed() && !t.isExpired())
                .orElse(false);
    }

    public void validatePasswordComplexity(String password) {
        if (password == null || password.trim().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter (A-Z).");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter (a-z).");
        }
        if (!password.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("Password must contain at least one number (0-9).");
        }
        if (!password.matches(".*[@#$%^&*!_~+=\\-\\?\\.].*")) {
            throw new IllegalArgumentException("Password must contain at least one allowed special symbol (@ # $ % ^ & * ! _ ~ + = - ? .).");
        }
    }

    @Transactional
    public boolean resetPassword(String tokenStr, String newPassword) {
        if (tokenStr == null || tokenStr.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Token and new password are required.");
        }

        validatePasswordComplexity(newPassword.trim());

        PasswordResetToken resetToken = resetTokenRepository.findByToken(tokenStr.trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired password reset token."));

        if (Boolean.TRUE.equals(resetToken.getIsUsed())) {
            throw new IllegalArgumentException("This password reset token has already been used.");
        }

        if (resetToken.isExpired()) {
            throw new IllegalArgumentException("This password reset token has expired. Please request a new link.");
        }

        User user = resetToken.getUser();
        String encodedPassword = passwordEncoder.encode(newPassword.trim());
        user.setPassword(encodedPassword);
        userRepository.save(user);

        resetToken.setIsUsed(true);
        resetTokenRepository.save(resetToken);

        System.out.println("[DEBUG-AUTH] Password successfully reset and BCrypt-encoded for user: " + user.getUsername());
        return true;
    }

    @Transactional
    public User registerStaffUser(String username, String fullName, String email, String role) {
        return registerStaffUser(username, null, fullName, email, role);
    }

    @Transactional
    public User registerStaffUser(String username, String password, String fullName, String email, String role) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full Name is required.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email address is required for staff account registration.");
        }

        String cleanUsername = username.trim();
        if (userRepository.findByUsername(cleanUsername).isPresent()) {
            throw new IllegalArgumentException("Username '" + cleanUsername + "' is already registered.");
        }

        // If password is specified, validate complexity; otherwise generate temporary random string
        String effectivePassword;
        if (password != null && !password.trim().isEmpty()) {
            validatePasswordComplexity(password.trim());
            effectivePassword = password.trim();
        } else {
            effectivePassword = UUID.randomUUID().toString();
        }

        User user = new User();
        user.setUsername(cleanUsername);
        user.setPassword(passwordEncoder.encode(effectivePassword));
        user.setFullName(fullName.trim());
        user.setEmail(email.trim());
        user.setRole(role != null && role.trim().equalsIgnoreCase("Admin") ? "Admin" : "Receptionist");
        user.setIsActive(true);

        System.out.println("[DEBUG-AUTH] Registering new staff member: username=" + cleanUsername + ", role=" + user.getRole());
        User savedUser = userRepository.save(user);

        // Generate password setup / reset token for newly created user
        resetTokenRepository.deleteByUser(savedUser);
        String setupTokenStr = "RST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(30);

        PasswordResetToken tokenEntity = new PasswordResetToken(setupTokenStr, savedUser, expiryDate);
        resetTokenRepository.save(tokenEntity);
        System.out.println("[DEBUG-AUTH] Generated Password Setup Token '" + setupTokenStr + "' for staff: " + cleanUsername);

        // Dispatch Welcome & Password Setup Email to the user
        emailNotificationService.sendStaffWelcomeSetupEmail(savedUser.getEmail(), savedUser.getFullName(), savedUser.getUsername(), savedUser.getRole(), setupTokenStr);

        return savedUser;
    }
}

