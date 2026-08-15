package com.dentalclinic.controller.api;

import com.dentalclinic.dto.SlotDTO;
import com.dentalclinic.service.SlotService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for Dynamic Time Slot generation API endpoints.
 * 
 * Layer: Presentation / API Controller Layer
 * Base Path: /api/slots
 */
@RestController
@RequestMapping("/api/slots")
@CrossOrigin(origins = "*")
public class SlotApiController {

    private final SlotService slotService;

    public SlotApiController(SlotService slotService) {
        this.slotService = slotService;
    }

    /**
     * GET /api/slots/available?dentistId=1&date=2026-08-10
     */
    @GetMapping("/available")
    public ResponseEntity<List<SlotDTO>> getAvailableSlots(
            @RequestParam("dentistId") Integer dentistId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<SlotDTO> slots = slotService.generateAvailableSlots(dentistId, date);
        return ResponseEntity.ok(slots);
    }
}
