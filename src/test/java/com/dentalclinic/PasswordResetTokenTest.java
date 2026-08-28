package com.dentalclinic;

import com.dentalclinic.entity.PasswordResetToken;
import com.dentalclinic.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PasswordResetToken entity domain logic and expiration calculations.
 */
public class PasswordResetTokenTest {

    private User sampleUser;

    @BeforeEach
    public void setUp() {
        sampleUser = new User("admin", "hashedpwd", "System Admin", "Admin", "admin@sunrisedental.com");
        sampleUser.setUserId(1);
    }

    @Test
    @DisplayName("Scenario 1: Constructor initializes default values correctly")
    public void testConstructorInitialization() {
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(30);
        PasswordResetToken token = new PasswordResetToken("RST-TEST1234", sampleUser, expiry);

        assertEquals("RST-TEST1234", token.getToken());
        assertEquals(sampleUser, token.getUser());
        assertEquals(expiry, token.getExpiryDate());
        assertFalse(token.getIsUsed());
        assertNotNull(token.getCreatedDate());
    }

    @Test
    @DisplayName("Scenario 2: Future expiry date returns false for isExpired()")
    public void testIsExpired_FutureDate_ReturnsFalse() {
        LocalDateTime futureExpiry = LocalDateTime.now().plusMinutes(15);
        PasswordResetToken token = new PasswordResetToken("RST-ACTIVE", sampleUser, futureExpiry);

        assertFalse(token.isExpired());
    }

    @Test
    @DisplayName("Scenario 3: Past expiry date returns true for isExpired()")
    public void testIsExpired_PastDate_ReturnsTrue() {
        LocalDateTime pastExpiry = LocalDateTime.now().minusMinutes(5);
        PasswordResetToken token = new PasswordResetToken("RST-EXPIRED", sampleUser, pastExpiry);

        assertTrue(token.isExpired());
    }

    @Test
    @DisplayName("Scenario 4: Setters and Getters update token state")
    public void testSettersAndGetters() {
        PasswordResetToken token = new PasswordResetToken();
        token.setTokenId(100L);
        token.setToken("RST-SETTER");
        token.setUser(sampleUser);
        token.setIsUsed(true);

        assertEquals(100L, token.getTokenId());
        assertEquals("RST-SETTER", token.getToken());
        assertEquals(sampleUser, token.getUser());
        assertTrue(token.getIsUsed());
    }

    @Test
    @DisplayName("Scenario 5: Default constructor leaves fields as null/default")
    public void testDefaultConstructor() {
        PasswordResetToken token = new PasswordResetToken();
        assertNull(token.getTokenId());
        assertNull(token.getToken());
        assertNull(token.getUser());
        assertNull(token.getExpiryDate());
        assertFalse(token.getIsUsed());
    }

    @Test
    @DisplayName("Scenario 6: Verify creation timestamp is populated automatically")
    public void testCreatedDatePopulated() {
        PasswordResetToken token = new PasswordResetToken("RST-TIMESTAMP", sampleUser, LocalDateTime.now().plusHours(1));
        assertNotNull(token.getCreatedDate());
        assertTrue(token.getCreatedDate().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}
