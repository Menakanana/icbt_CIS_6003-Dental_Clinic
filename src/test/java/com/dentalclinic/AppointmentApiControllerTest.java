package com.dentalclinic;

import com.dentalclinic.dto.BookingRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AppointmentApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Scenario 1: POST /api/appointments/book - Successful Booking Returns 201 Created")
    public void testBookAppointment_Success() throws Exception {
        BookingRequestDTO request = new BookingRequestDTO();
        request.setPatientId(1);
        request.setDentistId(1);
        request.setTreatmentTypeId(1);
        request.setAppointmentDate(LocalDate.now().plusDays(5));
        request.setStartTime(LocalTime.of(14, 0));
        request.setEndTime(LocalTime.of(14, 30));

        mockMvc.perform(post("/api/appointments/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.appointmentId").exists())
                .andExpect(jsonPath("$.tokenNumber").exists());
    }

    @Test
    @DisplayName("Scenario 2: GET /api/appointments/today - Returns Today's Roster List (200 OK)")
    public void testGetTodayAppointments() throws Exception {
        mockMvc.perform(get("/api/appointments/today"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Scenario 3: GET /api/appointments/99999 - Non-existent ID 404 Not Found")
    public void testGetAppointmentById_NotFound() throws Exception {
        mockMvc.perform(get("/api/appointments/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Appointment not found with ID: 99999"));
    }

    @Test
    @DisplayName("Scenario 4: POST /api/appointments/book - Validation failure with missing fields")
    public void testBookAppointment_InvalidFields() throws Exception {
        String invalidPayload = "{}";

        mockMvc.perform(post("/api/appointments/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPayload))
                .andExpect(status().isBadRequest());
    }
}
