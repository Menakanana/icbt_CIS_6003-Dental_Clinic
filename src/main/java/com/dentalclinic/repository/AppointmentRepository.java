package com.dentalclinic.repository;

import com.dentalclinic.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Repository interface for Appointment domain entity data operations.
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    List<Appointment> findByDentist_DentistIdAndAppointmentDateAndStatusNot(Integer dentistId,
            LocalDate appointmentDate, String status);

    List<Appointment> findByPatient_PatientIdAndAppointmentDateAndStatusNot(Integer patientId,
            LocalDate appointmentDate, String status);

    List<Appointment> findByAppointmentDateAndStatusNot(LocalDate appointmentDate, String status);

    List<Appointment> findByAppointmentDateAndStatusNotOrderByTokenNumberAsc(LocalDate appointmentDate, String status);

    List<Appointment> findByPatient_PatientIdOrderByAppointmentDateDesc(Integer patientId);

    List<Appointment> findByPatient_PatientIdOrderByAppointmentDateDescStartTimeDesc(Integer patientId);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.dentist.dentistId = :dentistId AND a.appointmentDate = :appointmentDate AND a.status != 'CANCELLED'")
    Integer countActiveAppointmentsForDentistOnDate(@Param("dentistId") Integer dentistId,
            @Param("appointmentDate") LocalDate appointmentDate);

    @Query("SELECT a FROM Appointment a WHERE a.dentist.dentistId = :dentistId AND a.appointmentDate = :appointmentDate AND a.status != 'CANCELLED' AND ((a.startTime < :endTime) AND (a.endTime > :startTime))")
    List<Appointment> findOverlappingAppointments(
            @Param("dentistId") Integer dentistId,
            @Param("appointmentDate") LocalDate appointmentDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);

    @Query("SELECT a FROM Appointment a WHERE LOWER(a.patient.patientName) LIKE LOWER(CONCAT('%', :query, '%')) OR a.patient.contactNumber LIKE CONCAT('%', :query, '%') OR a.patient.nic LIKE CONCAT('%', :query, '%') OR CAST(a.appointmentId AS string) LIKE CONCAT('%', :query, '%') OR CAST(a.appointmentNumber AS string) LIKE CONCAT('%', :query, '%') ORDER BY a.appointmentDate DESC, a.tokenNumber ASC")
    List<Appointment> searchAppointments(@Param("query") String query);
}
