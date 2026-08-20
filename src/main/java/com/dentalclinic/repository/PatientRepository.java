package com.dentalclinic.repository;

import com.dentalclinic.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Integer> {

    Optional<Patient> findByContactNumber(String contactNumber);

    List<Patient> findByContactNumberAndIsActiveTrue(String contactNumber);

    List<Patient> findByContactNumberContainingAndIsActiveTrue(String contactNumber);

    Optional<Patient> findByNic(String nic);

    List<Patient> findByIsActiveTrue();

    boolean existsByNicAndIsActiveTrue(String nic);

    boolean existsByContactNumberAndIsActiveTrue(String contactNumber);

    boolean existsByPatientNameIgnoreCaseAndDateOfBirthAndIsActiveTrue(String patientName, LocalDate dateOfBirth);

    boolean existsByPatientNameIgnoreCaseAndContactNumberAndIsActiveTrue(String patientName, String contactNumber);

    Optional<Patient> findFirstByNicAndIsActiveTrue(String nic);

    Optional<Patient> findFirstByPatientNameIgnoreCaseAndContactNumberAndIsActiveTrue(String patientName, String contactNumber);
}
