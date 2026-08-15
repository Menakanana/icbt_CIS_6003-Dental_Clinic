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
     * POST /api/dentists/schedules - Configure day-by-day Doctor shift availability.
     */
    @PostMapping("/schedules")
    public ResponseEntity<DentistScheduleDTO> saveSchedule(@Valid @RequestBody DentistScheduleDTO scheduleDTO) {
        DentistScheduleDTO saved = dentistService.saveSchedule(scheduleDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * POST /api/dentists/schedules/off-duty - Mark a Doctor OFF DUTY for a specific date.
     */
    @PostMapping("/schedules/off-duty")
    public ResponseEntity<java.util.Map<String, Object>> markDoctorOffDuty(
            @RequestParam("dentistId") Integer dentistId,
            @RequestParam("scheduleDate") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate scheduleDate) {
        dentistService.markDoctorOffDuty(dentistId, scheduleDate);
        return ResponseEntity.ok(java.util.Map.of("success", true, "message", "Doctor marked OFF DUTY for " + scheduleDate + "."));
    }

    /**
     * POST /api/dentists/schedules/on-duty - Mark a Doctor ON DUTY for a specific date.
     */
    @PostMapping("/schedules/on-duty")
    public ResponseEntity<java.util.Map<String, Object>> markDoctorOnDuty(
            @RequestParam("dentistId") Integer dentistId,
            @RequestParam("scheduleDate") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate scheduleDate) {
        dentistService.markDoctorOnDuty(dentistId, scheduleDate);
        return ResponseEntity.ok(java.util.Map.of("success", true, "message", "Doctor marked ON DUTY for " + scheduleDate + "."));
    }

    /**
     * GET /api/dentists/schedules/date - Fetch shift schedules for a specific date.
     */
    @GetMapping("/schedules/date")
    public ResponseEntity<List<DentistScheduleDTO>> getSchedulesByDate(
            @RequestParam("scheduleDate") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate scheduleDate) {
        List<DentistScheduleDTO> list = dentistService.getSchedulesForDate(scheduleDate);
        return ResponseEntity.ok(list);
    }

    /**
     * DELETE /api/dentists/schedules/{id} - Delete an individual shift session block.
     */
    @DeleteMapping("/schedules/{id}")
    public ResponseEntity<java.util.Map<String, Object>> deleteScheduleSession(@PathVariable("id") Integer id) {
        dentistService.deleteScheduleSession(id);
        return ResponseEntity.ok(java.util.Map.of("success", true, "message", "Shift session block removed successfully."));
    }

    /**
     * DELETE /api/dentists/{id} - Soft delete / Deactivate a Doctor profile.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<java.util.Map<String, Object>> deleteDentist(@PathVariable("id") Integer id) {
        dentistService.deleteDentist(id);
        return ResponseEntity.ok(java.util.Map.of("success", true, "message", "Doctor profile deleted successfully."));
    }
}
