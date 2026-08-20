package com.dentalclinic;

import com.dentalclinic.dto.AuthResponseDTO;
import com.dentalclinic.dto.LoginRequestDTO;
import com.dentalclinic.entity.PasswordResetToken;
import com.dentalclinic.entity.User;
import com.dentalclinic.exception.BadCredentialsException;
import com.dentalclinic.repository.UserRepository;
import com.dentalclinic.service.AuthService;
import com.dentalclinic.service.EmailNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Comprehensive JUnit 5 & Mockito Unit Test Suite for AuthService (Task C)
 * Covers positive, negative, disabled account, and boundary scenarios in complete isolation.
 */
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private com.dentalclinic.repository.PasswordResetTokenRepository resetTokenRepository;

    private EmailNotificationService emailNotificationService = new EmailNotificationService();

    private AuthService authService;

    private User sampleUser;

    @BeforeEach
    public void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, emailNotificationService, resetTokenRepository);
        sampleUser = new User(
                "receptionist",
                "$2a$12$e876...hashedpassword",
                "Sarah Jenkins",
                "Receptionist",
                "receptionist@sunrisedental.com"
        );
        sampleUser.setUserId(1);
        sampleUser.setIsActive(true);
    }

    @Test
    @DisplayName("Scenario 1: Successful Login - Returns Token, Role & Updates LastLogin")
    public void testAuthenticate_Success() {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO("receptionist", "recept123");
        when(userRepository.findByUsername("receptionist")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("recept123", sampleUser.getPassword())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        // Act
        AuthResponseDTO response = authService.authenticate(request);

        // Assert (JUnit 5 Assertions with Record accessors)
        assertNotNull(response);
        assertEquals("receptionist", response.username());
        assertEquals("Receptionist", response.role());
        assertEquals(1, response.userId());
        assertNotNull(response.token());
        assertTrue(response.token().startsWith("jwt-bearer-"));

        // Verify lastLogin timestamp was updated and saved
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertNotNull(userCaptor.getValue().getLastLogin());

        verify(userRepository, times(1)).findByUsername("receptionist");
        verify(passwordEncoder, times(1)).matches("recept123", sampleUser.getPassword());
    }

    @Test
    @DisplayName("Scenario 2: Failed Login - User Not Found")
    public void testAuthenticate_UserNotFound() {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO("non_existent_user", "password123");
        when(userRepository.findByUsername("non_existent_user")).thenReturn(Optional.empty());

        // Act & Assert
        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authService.authenticate(request)
        );

        assertEquals("Invalid username or password", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("non_existent_user");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Scenario 3: Failed Login - Incorrect Password Mismatch")
    public void testAuthenticate_PasswordMismatch() {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO("receptionist", "wrong_password");
        when(userRepository.findByUsername("receptionist")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("wrong_password", sampleUser.getPassword())).thenReturn(false);

        // Act & Assert
        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authService.authenticate(request)
        );

        assertEquals("Invalid username or password", exception.getMessage());
        verify(passwordEncoder, times(1)).matches("wrong_password", sampleUser.getPassword());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Scenario 4: Failed Login - Account Disabled (IsActive == false)")
    public void testAuthenticate_DisabledAccount() {
        // Arrange
        sampleUser.setIsActive(false); // Account deactivated by Admin
        LoginRequestDTO request = new LoginRequestDTO("receptionist", "recept123");
        when(userRepository.findByUsername("receptionist")).thenReturn(Optional.of(sampleUser));

        // Act & Assert
        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authService.authenticate(request)
        );

        assertEquals("Account is disabled. Please contact administrator.", exception.getMessage());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Scenario 5: Process Password Reset Request - Saves Token to DB & Dispatches Email")
    public void testProcessForgotPassword_Success() {
        // Arrange
        when(userRepository.findByUsername("receptionist")).thenReturn(Optional.of(sampleUser));

        // Act
        String result = authService.processForgotPassword("receptionist");

        // Assert
        assertTrue(result.contains("If an account matching 'receptionist' exists"));
        verify(resetTokenRepository, times(1)).deleteByUser(sampleUser);
        verify(resetTokenRepository, times(1)).save(any(com.dentalclinic.entity.PasswordResetToken.class));
    }

    @Test
    @DisplayName("Scenario 6: validatePasswordResetToken - Null or empty token returns false")
    public void testValidatePasswordResetToken_NullOrEmpty_ReturnsFalse() {
        assertFalse(authService.validatePasswordResetToken(null));
        assertFalse(authService.validatePasswordResetToken("   "));
    }

    @Test
    @DisplayName("Scenario 7: validatePasswordResetToken - Non-existent token returns false")
    public void testValidatePasswordResetToken_NotFound_ReturnsFalse() {
        when(resetTokenRepository.findByToken("RST-INVALID")).thenReturn(Optional.empty());
        assertFalse(authService.validatePasswordResetToken("RST-INVALID"));
    }

    @Test
    @DisplayName("Scenario 8: validatePasswordResetToken - Expired token returns false")
    public void testValidatePasswordResetToken_Expired_ReturnsFalse() {
        com.dentalclinic.entity.PasswordResetToken expiredToken = new com.dentalclinic.entity.PasswordResetToken(
                "RST-EXPIRED", sampleUser, java.time.LocalDateTime.now().minusMinutes(10)
        );
        when(resetTokenRepository.findByToken("RST-EXPIRED")).thenReturn(Optional.of(expiredToken));

        assertFalse(authService.validatePasswordResetToken("RST-EXPIRED"));
    }

    @Test
    @DisplayName("Scenario 9: resetPassword - Null or empty token/password throws IllegalArgumentException")
    public void testResetPassword_NullTokenOrPassword_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> authService.resetPassword("", "newpass"));
        assertThrows(IllegalArgumentException.class, () -> authService.resetPassword("RST-TOKEN", ""));
    }

    @Test
    @DisplayName("Scenario 10: resetPassword - Already used token throws IllegalArgumentException")
    public void testResetPassword_UsedToken_ThrowsException() {
        com.dentalclinic.entity.PasswordResetToken usedToken = new com.dentalclinic.entity.PasswordResetToken(
                "RST-USED", sampleUser, java.time.LocalDateTime.now().plusMinutes(20)
        );
        usedToken.setIsUsed(true);
        when(resetTokenRepository.findByToken("RST-USED")).thenReturn(Optional.of(usedToken));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> authService.resetPassword("RST-USED", "ComplexPass123!")
        );

        assertTrue(ex.getMessage().contains("already been used"));
    }

    @Test
    @DisplayName("Scenario 10b: resetPassword - Fails when password complexity criteria are missing")
    public void testResetPassword_PasswordComplexityFailures() {
        // Short length
        assertThrows(IllegalArgumentException.class, () -> authService.resetPassword("RST-VALID", "Ab1!"));
        // Missing Uppercase
        assertThrows(IllegalArgumentException.class, () -> authService.resetPassword("RST-VALID", "lowercase123!"));
        // Missing Lowercase
        assertThrows(IllegalArgumentException.class, () -> authService.resetPassword("RST-VALID", "UPPERCASE123!"));
        // Missing Number
        assertThrows(IllegalArgumentException.class, () -> authService.resetPassword("RST-VALID", "NoNumbersHere!"));
        // Missing Allowed Symbol
        assertThrows(IllegalArgumentException.class, () -> authService.resetPassword("RST-VALID", "NoSymbols123"));
    }

    @Test
    @DisplayName("Scenario 11: registerStaffUser - Successfully registers a new Receptionist staff account and sends welcome setup email")
    public void testRegisterStaffUser_Success() {
        when(userRepository.findByUsername("recept2")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encodedpassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User created = authService.registerStaffUser("recept2", "Kavindi Perera", "kavindi@sunrisedental.com", "Receptionist");

        assertNotNull(created);
        assertEquals("recept2", created.getUsername());
        assertEquals("Kavindi Perera", created.getFullName());
        assertEquals("Receptionist", created.getRole());
        assertEquals("$2a$10$encodedpassword", created.getPassword());
        assertTrue(created.getIsActive());

        verify(resetTokenRepository, times(1)).save(any(PasswordResetToken.class));
    }
}
