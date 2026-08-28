package com.dentalclinic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SlotApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Scenario 1: GET /api/slots/available - Fetch Available Slots Successfully")
    public void testGetAvailableSlots_Success() throws Exception {
        mockMvc.perform(get("/api/slots/available?dentistId=1&date=2026-10-15"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Scenario 2: GET /api/slots/available - Missing Required Dentist ID Returns 500 Server Error")
    public void testGetAvailableSlots_MissingParams() throws Exception {
        mockMvc.perform(get("/api/slots/available?date=2026-10-15"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Scenario 3: GET /api/slots/available - Invalid Dentist ID Returns Error Status")
    public void testGetAvailableSlots_InvalidDentist() throws Exception {
        mockMvc.perform(get("/api/slots/available?dentistId=99999&date=2026-10-15"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Scenario 4: GET /api/slots/available - Supports dd-MM-yyyy Date Format (15-10-2026)")
    public void testGetAvailableSlots_CustomDateFormat() throws Exception {
        mockMvc.perform(get("/api/slots/available?dentistId=1&date=15-10-2026"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Scenario 5: GET /api/slots/available - Supports dd/MM/yyyy Primary Date Format (15/10/2026)")
    public void testGetAvailableSlots_SlashDateFormat() throws Exception {
        mockMvc.perform(get("/api/slots/available?dentistId=1&date=15/10/2026"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }
}
