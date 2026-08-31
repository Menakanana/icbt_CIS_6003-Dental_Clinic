package com.dentalclinic;

import com.dentalclinic.dto.DentistDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DentistApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Scenario 1: GET /api/dentists - Retrieve All Active Dentists")
    public void testGetAllActiveDentists() throws Exception {
        mockMvc.perform(get("/api/dentists"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Scenario 2: GET /api/dentists/schedules - Fetch Dentist Schedules")
    public void testGetAllSchedules_Success() throws Exception {
        mockMvc.perform(get("/api/dentists/schedules"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Scenario 3: POST /api/dentists - Save New Dentist Profile Returns 201 Created")
    public void testSaveDentist_Success() throws Exception {
        DentistDTO dto = new DentistDTO(null, "Dr. Clara Oswald", "Periodontics", "0775551234", new BigDecimal("3000.00"));

        mockMvc.perform(post("/api/dentists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dentistName").value("Dr. Clara Oswald"));
    }

    @Test
    @DisplayName("Scenario 4: POST /api/dentists - Invalid Payload returns 400 Bad Request")
    public void testSaveDentist_InvalidPayload() throws Exception {
        String invalidPayload = "{}";

        mockMvc.perform(post("/api/dentists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPayload))
                .andExpect(status().isBadRequest());
    }
}
