package com.dentalclinic.service;

import com.dentalclinic.dto.SlotDTO;
import com.dentalclinic.entity.Appointment;
import com.dentalclinic.entity.Dentist;
import com.dentalclinic.entity.DentistSchedule;
import com.dentalclinic.repository.AppointmentRepository;
import com.dentalclinic.repository.DentistRepository;
import com.dentalclinic.repository.DentistScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for dynamic time slot generation, mathematical overlap evaluation,
 * and token sequence calculation.
 * 
 * Layer: Business Logic Layer
 */
@Service
public class SlotService {

    private final DentistRepository dentistRepository;
    private final DentistScheduleRepository scheduleRepository;
    private final AppointmentRepository appointmentRepository;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");

    @Autowired
    public SlotService(DentistRepository dentistRepository,
            DentistScheduleRepository scheduleRepository,
            AppointmentRepository appointmentRepository) {
        this.dentistRepository = dentistRepository;
        this.scheduleRepository = scheduleRepository;
        this.appointmentRepository = appointmentRepository;
    }

    /**
     * Generates non-overlapping 15/30-minute time slots for a selected Dentist and
     * Date.
     * 
     * @param dentistId    ID of the target dentist
     * @param selectedDate Date of appointment
     * @return List of SlotDTOs with availability flags and token numbers
     */
    public List<SlotDTO> generateAvailableSlots(Integer dentistId, LocalDate selectedDate) {
        if (selectedDate == null || selectedDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot generate time slots for a past date.");
        }

        dentistRepository.findById(dentistId)
                .orElseThrow(() -> new IllegalArgumentException("Dentist not found with ID: " + dentistId));

        // Step 1: Fetch dentist schedules for the selected date
        List<DentistSchedule> allSchedules = scheduleRepository
                .findByDentist_DentistIdAndScheduleDate(dentistId, selectedDate);

        List<DentistSchedule> activeSchedules = new ArrayList<>();
        if (allSchedules != null && !allSchedules.isEmpty()) {
            boolean anyExplicitOffDuty = allSchedules.stream().anyMatch(s -> Boolean.FALSE.equals(s.getIsActive()));
            activeSchedules = allSchedules.stream()
                    .filter(s -> Boolean.TRUE.equals(s.getIsActive()))
                    .sorted(java.util.Comparator.comparing(DentistSchedule::getSessionStartTime))
                    .collect(Collectors.toList());

            if (activeSchedules.isEmpty() && anyExplicitOffDuty) {
                // Doctor is explicitly OFF DUTY on this date -> return empty list of available slots
                return List.of();
            }
        }

        if (activeSchedules.isEmpty()) {
            // Default 1-session schedule if no database entry exists
            DentistSchedule defaultSchedule = new DentistSchedule();
            defaultSchedule.setSessionStartTime(LocalTime.of(9, 0));
            defaultSchedule.setSessionEndTime(LocalTime.of(13, 0));
            defaultSchedule.setSlotDurationMinutes(30);
            defaultSchedule.setMaxPatientsInSession(null);
            activeSchedules.add(defaultSchedule);
        }

        // Step 2: Fetch existing non-cancelled database appointments for overlap evaluation
        List<Appointment> existingAppointments = appointmentRepository
                .findByDentist_DentistIdAndAppointmentDateAndStatusNot(dentistId, selectedDate, "CANCELLED");

        // Step 3: Iterate through all active session blocks and generate time slots
        List<SlotDTO> slots = new ArrayList<>();
        int tokenIndex = 1;

        for (DentistSchedule schedule : activeSchedules) {
            LocalTime shiftStart = schedule.getSessionStartTime();
            LocalTime shiftEnd = schedule.getSessionEndTime();
            int slotDurationMinutes = 30;
            int maxPatientsInBlock = 999;

            if (schedule.getMaxPatientsInSession() != null && schedule.getMaxPatientsInSession() > 0) {
                maxPatientsInBlock = schedule.getMaxPatientsInSession();
                long totalMins = java.time.Duration.between(shiftStart, shiftEnd).toMinutes();
                if (totalMins > 0) {
                    slotDurationMinutes = (int) Math.max(5, totalMins / maxPatientsInBlock);
                }
            } else if (schedule.getSlotDurationMinutes() != null && schedule.getSlotDurationMinutes() > 0) {
                slotDurationMinutes = schedule.getSlotDurationMinutes();
            }

            LocalTime currentTime = shiftStart;
            int blockTokenCount = 0;

            while (blockTokenCount < maxPatientsInBlock && (currentTime.plusMinutes(slotDurationMinutes).isBefore(shiftEnd)
                    || currentTime.plusMinutes(slotDurationMinutes).equals(shiftEnd))) {
                LocalTime slotStart = currentTime;
                LocalTime slotEnd = currentTime.plusMinutes(slotDurationMinutes);

                // Mathematical Overlap Evaluation: (slotStart < appEnd) AND (slotEnd > appStart)
                boolean isOverlapping = existingAppointments.stream()
                        .anyMatch(app -> (slotStart.isBefore(app.getEndTime())) && (slotEnd.isAfter(app.getStartTime())));

                // Prevent booking slots in the past if selectedDate is today
                boolean isPastTime = false;
                if (selectedDate.equals(LocalDate.now()) && slotStart.isBefore(LocalTime.now())) {
                    isPastTime = true;
                }

                boolean available = (!isOverlapping) && (!isPastTime);
                String formattedTime = slotStart.format(TIME_FORMATTER);

                slots.add(new SlotDTO(
                        tokenIndex,
                        tokenIndex,
                        slotStart,
                        slotEnd,
                        formattedTime,
                        available));

                currentTime = slotEnd;
                tokenIndex++;
                blockTokenCount++;
            }
        }

        return slots;
    }
}
