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

    @Autowired
    public AppointmentApiController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
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
}
