package com.dentalclinic.repository;

import com.dentalclinic.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {

    Optional<Patient> findByContactNumber(String contactNumber);

    Optional<Patient> findByNic(String nic);

    List<Patient> findByIsActiveTrue();
}
