package com.dentalclinic.repository;

import com.dentalclinic.entity.DentistSessionSlots;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for DentistSessionSlots domain entity data operations.
 */
@Repository
public interface DentistSessionSlotsRepository extends JpaRepository<DentistSessionSlots, Integer> {
    List<DentistSessionSlots> findBySchedule_ScheduleIdOrderBySlotNumberAsc(Integer scheduleId);
    List<DentistSessionSlots> findByDentist_DentistIdOrderBySlotNumberAsc(Integer dentistId);
}
