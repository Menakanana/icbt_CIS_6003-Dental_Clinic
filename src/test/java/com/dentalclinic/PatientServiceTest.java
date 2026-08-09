package com.dentalclinic;

import com.dentalclinic.dto.PatientDTO;
import com.dentalclinic.entity.Patient;
import com.dentalclinic.exception.ResourceNotFoundException;
import com.dentalclinic.repository.PatientRepository;
import com.dentalclinic.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pure JUnit 5 & Mockito Unit Test Suite for PatientService (Task C)
 * Tests patient registration, retrieval, and exception scenarios in complete isolation.
 */
@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient patient1;
    private Patient patient2;

    @BeforeEach
    public void setUp() {
        patient1 = new Patient("John Doe", "0771234567", "johndoe@gmail.com", "123 Galle Rd", "199012345678");
        patient1.setPatientId(101);
        patient1.setDateOfBirth(LocalDate.of(1990, 5, 15));
        patient1.setGender("M");

        patient2 = new Patient("Jane Smith", "0779876543", "janesmith@gmail.com", "456 Kandy Rd", "199598765432");
        patient2.setPatientId(102);
        patient2.setDateOfBirth(LocalDate.of(1995, 8, 20));
        patient2.setGender("F");
    }

    @Test
    @DisplayName("Scenario 1: Register New Patient Successfully")
    public void testRegisterPatient_Success() {
        // Arrange
        PatientDTO inputDto = new PatientDTO(null, "John Doe", "0771234567", "johndoe@gmail.com", "123 Galle Rd", "199012345678", LocalDate.of(1990, 5, 15), "M");

        when(patientRepository.save(any(Patient.class))).thenReturn(patient1);

        // Act
        PatientDTO result = patientService.registerPatient(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(101, result.patientId());
        assertEquals("John Doe", result.patientName());
        assertEquals("0771234567", result.contactNumber());
        assertEquals("199012345678", result.nic());

        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    @DisplayName("Scenario 2: Get All Active Patients Returns Patient List")
    public void testGetAllActivePatients_Success() {
        // Arrange
        when(patientRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(patient1, patient2));

        // Act
        List<PatientDTO> resultList = patientService.getAllActivePatients();

        // Assert
        assertNotNull(resultList);
        assertEquals(2, resultList.size());
        assertEquals("John Doe", resultList.get(0).patientName());
        assertEquals("Jane Smith", resultList.get(1).patientName());

        verify(patientRepository, times(1)).findByIsActiveTrue();
    }

    @Test
    @DisplayName("Scenario 3: Get Patient by Existing ID Returns PatientDTO")
    public void testGetPatientById_Success() {
        // Arrange
        when(patientRepository.findById(101)).thenReturn(Optional.of(patient1));

        // Act
        PatientDTO result = patientService.getPatientById(101);

        // Assert
        assertNotNull(result);
        assertEquals(101, result.patientId());
        assertEquals("John Doe", result.patientName());

        verify(patientRepository, times(1)).findById(101);
    }

    @Test
    @DisplayName("Scenario 4: Get Patient by Non-Existing ID Throws ResourceNotFoundException")
    public void testGetPatientById_NotFound() {
        // Arrange
        when(patientRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> patientService.getPatientById(999)
        );

        assertEquals("Patient not found with ID: 999", exception.getMessage());
        verify(patientRepository, times(1)).findById(999);
    }
}
