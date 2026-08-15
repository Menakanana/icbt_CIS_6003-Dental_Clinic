package com.dentalclinic.repository;

import com.dentalclinic.entity.TreatmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for TreatmentType domain entity data operations.
 */
@Repository
public interface TreatmentTypeRepository extends JpaRepository<TreatmentType, Integer> {
    List<TreatmentType> findByIsActiveTrue();
    boolean existsByTreatmentNameIgnoreCaseAndIsActiveTrue(String treatmentName);
}
