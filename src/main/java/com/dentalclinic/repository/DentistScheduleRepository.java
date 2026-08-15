package com.dentalclinic.repository;

import com.dentalclinic.entity.DentistSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for DentistSchedule domain entity data operations.
 */
@Repository
public interface DentistScheduleRepository extends JpaRepository<DentistSchedule, Integer> {
    List<DentistSchedule> findByDentist_DentistIdAndScheduleDateAndIsActiveTrue(Integer dentistId, LocalDate scheduleDate);
    List<DentistSchedule> findByDentist_DentistIdAndScheduleDateAndIsActiveTrueOrderBySessionStartTimeAsc(Integer dentistId, LocalDate scheduleDate);
    List<DentistSchedule> findByDentist_DentistIdAndScheduleDate(Integer dentistId, LocalDate scheduleDate);
    Optional<DentistSchedule> findFirstByDentist_DentistIdAndScheduleDateAndIsActiveTrue(Integer dentistId, LocalDate scheduleDate);
    Optional<DentistSchedule> findFirstByDentist_DentistIdAndScheduleDate(Integer dentistId, LocalDate scheduleDate);
    List<DentistSchedule> findByScheduleDate(LocalDate scheduleDate);
}
