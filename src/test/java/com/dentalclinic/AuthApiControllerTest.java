package com.dentalclinic;

import com.dentalclinic.dto.LoginRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * JUnit 5 + MockMvc API Test Suite for AuthApiController.
 * Tests REST API endpoints, HTTP status codes, and JSON response formats.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AuthApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("API Test: POST /api/auth/login Returns 200 OK with AuthResponseDTO")
    public void testSuccessfulLogin() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("receptionist", "recept123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("receptionist"))
                .andExpect(jsonPath("$.role").value("Receptionist"))
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @DisplayName("API Test: POST /api/auth/login Invalid Password Returns 401 Unauthorized")
    public void testFailedLoginInvalidPassword() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("receptionist", "wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }
}
