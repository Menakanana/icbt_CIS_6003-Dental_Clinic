package com.dentalclinic.service;

import com.dentalclinic.dto.DentistDTO;
import com.dentalclinic.dto.DentistScheduleDTO;
import com.dentalclinic.entity.Dentist;
import com.dentalclinic.entity.DentistSchedule;
import com.dentalclinic.exception.ResourceNotFoundException;
import com.dentalclinic.repository.DentistRepository;
import com.dentalclinic.repository.DentistScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service handling Doctor profile management and dynamic day-by-day
 * availability schedules.
 * 
 * Layer: Business Logic Layer
 */
@Service
public class DentistService {

    private final DentistRepository dentistRepository;
    private final DentistScheduleRepository scheduleRepository;

    public DentistService(DentistRepository dentistRepository, DentistScheduleRepository scheduleRepository) {
        this.dentistRepository = dentistRepository;
        this.scheduleRepository = scheduleRepository;
    }

    /**
     * Saves or updates a Doctor profile.
     */
    @Transactional
    public DentistDTO saveDentist(DentistDTO dto) {
        Dentist dentist;
        if (dto.getDentistId() != null) {
            dentist = dentistRepository.findById(dto.getDentistId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Dentist not found with ID: " + dto.getDentistId()));
            dentist.setDentistName(dto.getDentistName());
            dentist.setSpecialization(dto.getSpecialization());
            dentist.setContactNumber(dto.getContactNumber());
            dentist.setConsultationFee(dto.getConsultationFee());
        } else {
            if (dentistRepository.existsByDentistNameIgnoreCaseAndIsActiveTrue(dto.getDentistName())) {
                throw new IllegalArgumentException("A Doctor profile for '" + dto.getDentistName() + "' already exists in the system.");
            }
            dentist = new Dentist(
                    dto.getDentistName(),
                    dto.getSpecialization(),
                    dto.getContactNumber(),
                    dto.getConsultationFee());
        }
        Dentist saved = dentistRepository.save(dentist);
        return new DentistDTO(saved.getDentistId(), saved.getDentistName(), saved.getSpecialization(),
                saved.getContactNumber(), saved.getConsultationFee());
    }

    /**
     * Saves or updates a Doctor's day-by-day shift availability schedule.
     */
    @Transactional
    public DentistScheduleDTO saveSchedule(DentistScheduleDTO dto) {
        if (dto.getScheduleDate() != null && dto.getScheduleDate().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("Shift schedule date cannot be in the past.");
        }
        if (dto.getSessionStartTime() != null && dto.getSessionEndTime() != null
                && !dto.getSessionEndTime().isAfter(dto.getSessionStartTime())) {
            throw new IllegalArgumentException("Shift End Time must be strictly after Start Time.");
        }

        Dentist dentist = dentistRepository.findById(dto.getDentistId())
                .orElseThrow(() -> new ResourceNotFoundException("Dentist not found with ID: " + dto.getDentistId()));

        DentistSchedule schedule = scheduleRepository
                .findFirstByDentist_DentistIdAndScheduleDateAndIsActiveTrue(dto.getDentistId(), dto.getScheduleDate())
                .orElseGet(DentistSchedule::new);

        schedule.setDentist(dentist);
        schedule.setScheduleDate(dto.getScheduleDate());
        schedule.setSessionStartTime(dto.getSessionStartTime());
        schedule.setSessionEndTime(dto.getSessionEndTime());

        int maxPatients = (dto.getMaxPatientsInSession() != null && dto.getMaxPatientsInSession() > 0)
                ? dto.getMaxPatientsInSession()
                : 10;
        schedule.setMaxPatientsInSession(maxPatients);

        long totalMins = java.time.Duration.between(dto.getSessionStartTime(), dto.getSessionEndTime()).toMinutes();
        int calculatedSlotMins = (int) Math.max(5, totalMins / maxPatients);
        schedule.setSlotDurationMinutes(calculatedSlotMins);

        schedule.setIsActive(true);

        DentistSchedule saved = scheduleRepository.save(schedule);

        DentistScheduleDTO responseDTO = new DentistScheduleDTO(
                saved.getDentist().getDentistId(),
                saved.getScheduleDate(),
                saved.getSessionStartTime(),
                saved.getSessionEndTime(),
                saved.getSlotDurationMinutes());
        responseDTO.setScheduleId(saved.getScheduleId());
        responseDTO.setDentistName(saved.getDentist().getDentistName());
        responseDTO.setMaxPatientsInSession(saved.getMaxPatientsInSession());
        return responseDTO;
    }

    /**
     * Fetches all active Doctor profiles.
     */
    public List<DentistDTO> getAllActiveDentists() {
        return dentistRepository.findByIsActiveTrue()
                .stream()
                .map(d -> new DentistDTO(d.getDentistId(), d.getDentistName(), d.getSpecialization(),
                        d.getContactNumber(), d.getConsultationFee()))
                .collect(Collectors.toList());
    }

    /**
     * Fetches all active shift schedules.
     */
    public List<DentistScheduleDTO> getAllActiveSchedules() {
        return scheduleRepository.findAll()
                .stream()
                .filter(s -> Boolean.TRUE.equals(s.getIsActive()))
                .map(s -> {
                    DentistScheduleDTO dto = new DentistScheduleDTO(
                            s.getDentist().getDentistId(),
                            s.getScheduleDate(),
                            s.getSessionStartTime(),
                            s.getSessionEndTime(),
                            s.getSlotDurationMinutes());
                    dto.setScheduleId(s.getScheduleId());
                    dto.setDentistName(s.getDentist().getDentistName());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
