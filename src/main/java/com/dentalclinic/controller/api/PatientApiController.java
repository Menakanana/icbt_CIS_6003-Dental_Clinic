package com.dentalclinic.controller.api;

import com.dentalclinic.dto.PatientDTO;
import com.dentalclinic.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Patient Management API endpoints.
 * 
 * Layer: Presentation / API Controller Layer
 * Base URL: /api/patients
 * Handles: Creating and fetching patient records via RESTful JSON.
 */
@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
public class PatientApiController {

    private final PatientService patientService;

    @Autowired
    public PatientApiController(PatientService patientService) {
        this.patientService = patientService;
    }

    /**
     * POST /api/patients - Register a new patient.
     * 
     * @param patientDTO Input JSON payload
     * @return 201 Created with saved PatientDTO
     */
    @PostMapping
    public ResponseEntity<PatientDTO> registerPatient(@Valid @RequestBody PatientDTO patientDTO) {
        PatientDTO created = patientService.registerPatient(patientDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * GET /api/patients - Fetch all active patients.
     * 
     * @return 200 OK with list of patients
     */
    @GetMapping
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        List<PatientDTO> list = patientService.getAllActivePatients();
        return ResponseEntity.ok(list);
    }

    /**
     * GET /api/patients/{id} - Fetch a single patient by ID.
     * 
     * @param id Patient ID
     * @return 200 OK with PatientDTO or 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable("id") Integer id) {
        PatientDTO patient = patientService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }
}
