package com.dentalclinic;

import com.dentalclinic.dto.BookingRequestDTO;
import com.dentalclinic.service.AppointmentService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BillingApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppointmentService appointmentService;

    @Test
    @DisplayName("Scenario 1: GET /api/billing/calculate/99999 - Non-existent Appointment 404 Not Found")
    public void testGetBillingReceipt_NotFound() throws Exception {
        mockMvc.perform(get("/api/billing/calculate/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Appointment not found with ID: 99999"));
    }

    @Test
    @DisplayName("Scenario 2: GET /api/billing/calculate/1 - Negative Discount Exception Handling 500 Server Error")
    public void testGetBillingReceipt_NegativeDiscount() throws Exception {
        mockMvc.perform(get("/api/billing/calculate/1?discount=-100"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Scenario 3: GET /api/billing/calculate/{id} - Valid Appointment Bill Calculation")
    public void testGetBillingReceipt_Success() throws Exception {
        BookingRequestDTO request = new BookingRequestDTO();
        request.setPatientId(1);
        request.setDentistId(1);
        request.setTreatmentTypeId(1);
        request.setAppointmentDate(LocalDate.now().plusDays(10));
        request.setStartTime(LocalTime.of(15, 0));
        request.setEndTime(LocalTime.of(15, 30));

        var ticket = appointmentService.bookAppointment(request);

        mockMvc.perform(get("/api/billing/calculate/" + ticket.getAppointmentId() + "?discount=100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentId").value(ticket.getAppointmentId()))
                .andExpect(jsonPath("$.discountAmount").value(100.00))
                .andExpect(jsonPath("$.totalAmount").exists());
    }

    @Test
    @DisplayName("Scenario 4: POST /api/billing/send-email/{id} - Dispatch Receipt Email API")
    public void testSendReceiptEmail_Success() throws Exception {
        BookingRequestDTO request = new BookingRequestDTO();
        request.setPatientId(1);
        request.setDentistId(1);
        request.setTreatmentTypeId(1);
        request.setAppointmentDate(LocalDate.now().plusDays(11));
        request.setStartTime(LocalTime.of(16, 0));
        request.setEndTime(LocalTime.of(16, 30));

        var ticket = appointmentService.bookAppointment(request);

        org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder postReq = 
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/billing/send-email/" + ticket.getAppointmentId());

        mockMvc.perform(postReq)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Scenario 5: GET /api/billing/appointment/{id} - Retrieve Appointment Details for Billing Card")
    public void testGetAppointmentBillingDetails_Success() throws Exception {
        BookingRequestDTO request = new BookingRequestDTO();
        request.setPatientId(1);
        request.setDentistId(1);
        request.setTreatmentTypeId(1);
        request.setAppointmentDate(LocalDate.now().plusDays(12));
        request.setStartTime(LocalTime.of(17, 0));
        request.setEndTime(LocalTime.of(17, 30));

        var ticket = appointmentService.bookAppointment(request);

        mockMvc.perform(get("/api/billing/appointment/" + ticket.getAppointmentId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentId").value(ticket.getAppointmentId()))
                .andExpect(jsonPath("$.patientName").exists())
                .andExpect(jsonPath("$.previousPaidAmount").exists());
    }

    @Test
    @DisplayName("Scenario 6: POST /api/billing/settle - Complete Multi-Procedure Settlement API")
    public void testSettleBill_Success() throws Exception {
        BookingRequestDTO request = new BookingRequestDTO();
        request.setPatientId(1);
        request.setDentistId(1);
        request.setTreatmentTypeId(1);
        request.setAppointmentDate(LocalDate.now().plusDays(13));
        request.setStartTime(LocalTime.of(18, 0));
        request.setEndTime(LocalTime.of(18, 30));

        var ticket = appointmentService.bookAppointment(request);

        String jsonPayload = """
                {
                    "appointmentId": %d,
                    "treatmentTypeIds": [1],
                    "discount": 200.00,
                    "paymentMethod": "Credit/Debit Card"
                }
                """.formatted(ticket.getAppointmentId());

        org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder postReq =
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/billing/settle")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(jsonPayload);

        mockMvc.perform(postReq)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentId").value(ticket.getAppointmentId()))
                .andExpect(jsonPath("$.discountAmount").value(200.00))
                .andExpect(jsonPath("$.billStage").value("FINAL_SETTLED"));
    }
}
