package com.dentalclinic.controller.api;

import com.dentalclinic.dto.AppointmentTicketDTO;
import com.dentalclinic.dto.BookingRequestDTO;
import com.dentalclinic.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Appointment Booking & Ticket generation API endpoints.
 * 
 * Layer: Presentation / API Controller Layer
 * Base Path: /api/appointments
 */
@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentApiController {

    private final AppointmentService appointmentService;
    private final com.dentalclinic.service.EmailNotificationService emailNotificationService;

    @Autowired
    public AppointmentApiController(AppointmentService appointmentService,
                                     @Autowired(required = false) com.dentalclinic.service.EmailNotificationService emailNotificationService) {
        this.appointmentService = appointmentService;
        this.emailNotificationService = emailNotificationService;
    }

    /**
     * POST /api/appointments/book - Book appointment (Supports Existing Patient or 1-step Quick Add Patient).
     */
    @PostMapping("/book")
    public ResponseEntity<AppointmentTicketDTO> bookAppointment(@Valid @RequestBody BookingRequestDTO bookingRequest) {
        AppointmentTicketDTO ticket = appointmentService.bookAppointment(bookingRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }

    /**
     * GET /api/appointments/today - Fetch all scheduled appointments for today.
     */
    @GetMapping("/today")
    public ResponseEntity<List<AppointmentTicketDTO>> getTodayAppointments() {
        List<AppointmentTicketDTO> list = appointmentService.getTodayAppointments();
        return ResponseEntity.ok(list);
    }

    /**
     * GET /api/appointments/date - Fetch all scheduled appointments for a specific date.
     */
    @GetMapping("/date")
    public ResponseEntity<List<AppointmentTicketDTO>> getAppointmentsByDate(
            @RequestParam("date") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate date) {
        List<AppointmentTicketDTO> list = appointmentService.getAppointmentsByDate(date);
        return ResponseEntity.ok(list);
    }

    /**
     * GET /api/appointments/search?q=query&date=YYYY-MM-DD - Search appointments by patient name, phone, NIC, APT ID, or date.
     */
    @GetMapping("/search")
    public ResponseEntity<List<AppointmentTicketDTO>> searchAppointments(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "date", required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate date) {
        List<AppointmentTicketDTO> list = appointmentService.searchAppointments(query, date);
        return ResponseEntity.ok(list);
    }

    /**
     * GET /api/appointments/{id} - Search single appointment details by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentTicketDTO> getAppointmentById(@PathVariable("id") Integer id) {
        AppointmentTicketDTO ticket = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(ticket);
    }

    /**
     * POST /api/appointments/{id}/reschedule - Reschedule existing appointment to new date/time/dentist.
     */
    @PostMapping("/{id}/reschedule")
    public ResponseEntity<AppointmentTicketDTO> rescheduleAppointment(
            @PathVariable("id") Integer id,
            @RequestParam(name = "dentistId", required = false) Integer newDentistId,
            @RequestParam("date") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate newDate,
            @RequestParam(name = "startTime", required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.TIME) java.time.LocalTime newStartTime,
            @RequestParam(name = "tokenNumber", required = false) Integer tokenNumber) {
        AppointmentTicketDTO updated = appointmentService.rescheduleAppointment(id, newDentistId, newDate, newStartTime, null, tokenNumber);
        return ResponseEntity.ok(updated);
    }

    /**
     * POST /api/appointments/{id}/send-email - Dispatch ticket confirmation email.
     */
    @PostMapping("/{id}/send-email")
    public ResponseEntity<java.util.Map<String, Object>> sendTicketEmail(
            @PathVariable("id") Integer id,
            @RequestParam(name = "email", required = false) String customEmail) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        try {
            AppointmentTicketDTO ticket = appointmentService.getAppointmentById(id);
            String recipient = (customEmail != null && !customEmail.isBlank()) ? customEmail : ticket.getPatientEmail();
            boolean sent = (emailNotificationService != null) && emailNotificationService.sendBookingConfirmationEmail(ticket, recipient);
            response.put("success", sent);
            response.put("message", sent ? "Appointment ticket email dispatched successfully." : "Ticket email logged in console mode.");
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            response.put("success", false);
            response.put("message", ex.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
