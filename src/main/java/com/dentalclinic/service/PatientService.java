package com.dentalclinic.service;

import com.dentalclinic.dto.PatientDTO;
import com.dentalclinic.entity.Patient;
import com.dentalclinic.exception.ResourceNotFoundException;
import com.dentalclinic.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Managing Patient operations.
 * 
 * Layer: Service / Business Logic Layer
 * Role: Encapsulates patient registration, querying, and DTO <-> Entity conversions.
 */
@Service
public class PatientService {

    private final PatientRepository patientRepository;

    @Autowired
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * Registers a new patient into the clinic database.
     * 
     * @param dto Input patient data transfer object (Java 17 Record)
     * @return PatientDTO Saved patient details including generated ID
     */
    public PatientDTO registerPatient(PatientDTO dto) {
        if (dto.dateOfBirth() != null && dto.dateOfBirth().isAfter(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("Patient Date of Birth cannot be in the future.");
        }

        String nic = dto.nic() != null ? dto.nic().trim() : "";
        String name = dto.patientName() != null ? dto.patientName().trim() : "";

        // Check 1: Unique NIC check (only when NIC is provided)
        if (!nic.isEmpty() && patientRepository.existsByNicAndIsActiveTrue(nic)) {
            throw new IllegalArgumentException("A patient with NIC '" + nic + "' is already registered.");
        }

        // Check 2: Compound Duplicate check for Minors / Patients Without NIC (Same Name + Same DOB)
        if (nic.isEmpty() && !name.isEmpty() && dto.dateOfBirth() != null) {
            if (patientRepository.existsByPatientNameIgnoreCaseAndDateOfBirthAndIsActiveTrue(name, dto.dateOfBirth())) {
                throw new IllegalArgumentException("A patient named '" + name + "' born on " + dto.dateOfBirth() + " is already registered.");
            }
        }

        // Map DTO record accessors to Entity
        Patient patient = new Patient(
                dto.patientName(),
                dto.contactNumber(),
                dto.email(),
                dto.address(),
                dto.nic()
        );
        patient.setDateOfBirth(dto.dateOfBirth());
        patient.setGender(dto.gender());

        // Save entity to database via Repository
        Patient saved = patientRepository.save(patient);

        // Map saved entity back to DTO record
        return mapToDTO(saved);
    }

    /**
     * Retrieves all active patients from the database.
     * 
     * @return List of PatientDTO records
     */
    public List<PatientDTO> getAllActivePatients() {
        return patientRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a single patient by ID.
     * 
     * @param patientId ID of the patient
     * @return PatientDTO record
     * @throws ResourceNotFoundException if patient ID does not exist
     */
    public PatientDTO getPatientById(Integer patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));
        return mapToDTO(patient);
    }

    /**
     * Helper method to map a Patient Entity to a PatientDTO record.
     */
    private PatientDTO mapToDTO(Patient entity) {
        return new PatientDTO(
                entity.getPatientId(),
                entity.getPatientName(),
                entity.getContactNumber(),
                entity.getEmail(),
                entity.getAddress(),
                entity.getNic(),
                entity.getDateOfBirth(),
                entity.getGender()
        );
    }
}
