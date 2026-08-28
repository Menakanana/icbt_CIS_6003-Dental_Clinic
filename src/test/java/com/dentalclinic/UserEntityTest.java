package com.dentalclinic;

import com.dentalclinic.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for User entity domain model.
 */
public class UserEntityTest {

    @Test
    @DisplayName("Scenario 1: Constructor sets all fields correctly")
    public void testUserConstructor() {
        User u = new User("receptionist", "encodedpwd", "Sarah Jenkins", "Receptionist", "sarah@sunrisedental.com");

        assertEquals("receptionist", u.getUsername());
        assertEquals("encodedpwd", u.getPassword());
        assertEquals("Sarah Jenkins", u.getFullName());
        assertEquals("Receptionist", u.getRole());
        assertEquals("sarah@sunrisedental.com", u.getEmail());
        assertTrue(u.getIsActive());
        assertNotNull(u.getCreatedDate());
    }

    @Test
    @DisplayName("Scenario 2: Setters update fields properly")
    public void testUserSetters() {
        User u = new User();
        u.setUserId(10);
        u.setUsername("admin2");
        u.setPassword("newpass");
        u.setRole("Admin");
        u.setIsActive(false);
        LocalDateTime now = LocalDateTime.now();
        u.setLastLogin(now);

        assertEquals(10, u.getUserId());
        assertEquals("admin2", u.getUsername());
        assertEquals("newpass", u.getPassword());
        assertEquals("Admin", u.getRole());
        assertFalse(u.getIsActive());
        assertEquals(now, u.getLastLogin());
    }

    @Test
    @DisplayName("Scenario 3: Toggle active status")
    public void testActiveStatusToggle() {
        User u = new User();
        u.setIsActive(true);
        assertTrue(u.getIsActive());

        u.setIsActive(false);
        assertFalse(u.getIsActive());
    }

    @Test
    @DisplayName("Scenario 4: Nullable fields in default constructor")
    public void testDefaultConstructor() {
        User u = new User();
        assertNull(u.getUserId());
        assertNull(u.getUsername());
        assertNull(u.getPassword());
        assertNull(u.getEmail());
        assertNull(u.getLastLogin());
    }
}
