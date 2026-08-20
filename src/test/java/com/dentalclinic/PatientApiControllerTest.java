package com.dentalclinic;

import com.dentalclinic.dto.PatientDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * JUnit 5 + MockMvc API Test Suite for PatientApiController.
 * Tests REST API endpoints, DTO validations, HTTP status codes, and JSON response formats.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class PatientApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("API Test: POST /api/patients Creates Patient (201 Created)")
    public void testRegisterPatient_Success() throws Exception {
        PatientDTO request = new PatientDTO(null, "Alice Cooper", "0778889999", "alice@gmail.com", "789 Kandy Rd", "199255556666", LocalDate.of(1992, 4, 10), "F");

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.patientId").exists())
                .andExpect(jsonPath("$.patientName").value("Alice Cooper"))
                .andExpect(jsonPath("$.contactNumber").value("0778889999"));
    }

    @Test
    @DisplayName("API Test: POST /api/patients Blank Name & Bad Format Fails Validation (400 Bad Request)")
    public void testRegisterPatient_ValidationError() throws Exception {
        PatientDTO invalidRequest = new PatientDTO(null, "", "invalid_phone", "invalid-email", "Address", "invalid_nic", LocalDate.now().plusDays(5), "X");

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.fieldErrors.patientName").exists())
                .andExpect(jsonPath("$.fieldErrors.contactNumber").exists());
    }

    @Test
    @DisplayName("API Test: GET /api/patients Returns Patient List (200 OK)")
    public void testGetAllPatients_Success() throws Exception {
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("API Test: GET /api/patients/1 Returns Seeded Sample Patient (200 OK)")
    public void testGetPatientById_Success() throws Exception {
        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId").value(1))
                .andExpect(jsonPath("$.patientName").value("John Doe"));
    }

    @Test
    @DisplayName("API Test: GET /api/patients/99999 Returns 404 Not Found")
    public void testGetPatientById_NotFound() throws Exception {
        mockMvc.perform(get("/api/patients/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Patient not found with ID: 99999"));
    }

    @Test
    @DisplayName("API Test: GET /api/patients/search?phone=0771234567 Returns Matching Patients (200 OK)")
    public void testSearchPatientsByPhone_Success() throws Exception {
        mockMvc.perform(get("/api/patients/search").param("phone", "0771234567"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].contactNumber").value("0771234567"));
    }
}
