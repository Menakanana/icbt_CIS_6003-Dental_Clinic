package com.dentalclinic.controller.api;

import com.dentalclinic.dto.DentistDTO;
import com.dentalclinic.dto.DentistScheduleDTO;
import com.dentalclinic.service.DentistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Doctor Profile & Daily Schedule Web Services.
 * 
 * Layer: Presentation / API Controller Layer
 * Base Path: /api/dentists
 */
@RestController
@RequestMapping("/api/dentists")
@CrossOrigin(origins = "*")
public class DentistApiController {

    private final DentistService dentistService;

    public DentistApiController(DentistService dentistService) {
        this.dentistService = dentistService;
    }

    /**
     * GET /api/dentists - Fetch all active doctors.
     */
    @GetMapping
    public ResponseEntity<List<DentistDTO>> getAllDentists() {
        List<DentistDTO> list = dentistService.getAllActiveDentists();
        return ResponseEntity.ok(list);
    }

    /**
     * POST /api/dentists - Register or update a Doctor profile.
     */
    @PostMapping
    public ResponseEntity<DentistDTO> saveDentist(@Valid @RequestBody DentistDTO dentistDTO) {
        DentistDTO saved = dentistService.saveDentist(dentistDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * GET /api/dentists/schedules - Fetch all configured daily doctor shift
     * schedules.
     */
    @GetMapping("/schedules")
    public ResponseEntity<List<DentistScheduleDTO>> getAllSchedules() {
        List<DentistScheduleDTO> list = dentistService.getAllActiveSchedules();
        return ResponseEntity.ok(list);
    }

    /**
     * POST /api/dentists/schedules - Configure day-by-day Doctor shift
     * availability.
     */
    @PostMapping("/schedules")
    public ResponseEntity<DentistScheduleDTO> saveSchedule(@Valid @RequestBody DentistScheduleDTO scheduleDTO) {
        DentistScheduleDTO saved = dentistService.saveSchedule(scheduleDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
