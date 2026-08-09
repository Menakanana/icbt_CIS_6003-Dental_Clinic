package com.dentalclinic;

import com.dentalclinic.dto.AuthResponseDTO;
import com.dentalclinic.dto.LoginRequestDTO;
import com.dentalclinic.entity.User;
import com.dentalclinic.exception.BadCredentialsException;
import com.dentalclinic.repository.UserRepository;
import com.dentalclinic.service.AuthService;
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

    @InjectMocks
    private AuthService authService;

    private User sampleUser;

    @BeforeEach
    public void setUp() {
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
}
