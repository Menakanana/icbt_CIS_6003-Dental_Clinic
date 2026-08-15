package com.dentalclinic.repository;

import com.dentalclinic.entity.Dentist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Dentist domain entity data operations.
 */
@Repository
public interface DentistRepository extends JpaRepository<Dentist, Integer> {
    List<Dentist> findByIsActiveTrue();
    boolean existsByDentistNameIgnoreCaseAndIsActiveTrue(String dentistName);
    boolean existsByDentistNameIgnoreCaseAndContactNumberAndIsActiveTrue(String dentistName, String contactNumber);
}
