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

/**
 * Service for dynamic time slot generation, mathematical overlap evaluation, and token sequence calculation.
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
     * Generates non-overlapping 15/30-minute time slots for a selected Dentist and Date.
     * 
     * @param dentistId ID of the target dentist
     * @param selectedDate Date of appointment
     * @return List of SlotDTOs with availability flags and token numbers
     */
    public List<SlotDTO> generateAvailableSlots(Integer dentistId, LocalDate selectedDate) {
        Dentist dentist = dentistRepository.findById(dentistId)
                .orElseThrow(() -> new IllegalArgumentException("Dentist not found with ID: " + dentistId));

        // Step 1: Fetch dentist schedule or generate default shift hours (09:00 AM - 01:00 PM or 02:00 PM - 06:00 PM)
        LocalTime shiftStart = LocalTime.of(9, 0);
        LocalTime shiftEnd = LocalTime.of(13, 0);
        int slotDurationMinutes = 30;

        DentistSchedule schedule = scheduleRepository
                .findFirstByDentist_DentistIdAndScheduleDateAndIsActiveTrue(dentistId, selectedDate)
                .orElse(null);

        if (schedule != null) {
            shiftStart = schedule.getSessionStartTime();
            shiftEnd = schedule.getSessionEndTime();
            slotDurationMinutes = schedule.getSlotDurationMinutes();
        }

        // Step 2: Fetch existing non-cancelled database appointments for overlap evaluation
        List<Appointment> existingAppointments = appointmentRepository
                .findByDentist_DentistIdAndAppointmentDateAndStatusNot(dentistId, selectedDate, "CANCELLED");

        // Step 3: Loop and generate time slots with mathematical overlap evaluation
        List<SlotDTO> slots = new ArrayList<>();
        LocalTime currentTime = shiftStart;
        int tokenIndex = 1;

        while (currentTime.plusMinutes(slotDurationMinutes).isBefore(shiftEnd) || currentTime.plusMinutes(slotDurationMinutes).equals(shiftEnd)) {
            LocalTime slotStart = currentTime;
            LocalTime slotEnd = currentTime.plusMinutes(slotDurationMinutes);

            // Mathematical Overlap Evaluation: (slotStart < appEnd) AND (slotEnd > appStart)
            boolean isOverlapping = existingAppointments.stream().anyMatch(app -> 
                (slotStart.isBefore(app.getEndTime())) && (slotEnd.isAfter(app.getStartTime()))
            );

            // Prevent booking slots in the past if selectedDate is today
            boolean isPastTime = false;
            if (selectedDate.equals(LocalDate.now()) && slotStart.isBefore(LocalTime.now())) {
                isPastTime = true;
            }

            boolean available = (!isOverlapping) && (!isPastTime);

            String formattedTime = slotStart.format(TIME_FORMATTER) + " - " + slotEnd.format(TIME_FORMATTER);

            slots.add(new SlotDTO(
                    tokenIndex, // Using tokenIndex as slot ID identifier
                    tokenIndex,
                    slotStart,
                    slotEnd,
                    formattedTime,
                    available
            ));

            currentTime = slotEnd;
            tokenIndex++;
        }

        return slots;
    }
}
