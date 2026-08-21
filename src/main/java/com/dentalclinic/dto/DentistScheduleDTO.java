package com.dentalclinic.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for configuring day-by-day Doctor availability schedules.
 */
public class DentistScheduleDTO {

    private Integer scheduleId;

    @NotNull(message = "Dentist ID is required")
    private Integer dentistId;

    private String dentistName;

    @NotNull(message = "Schedule date is required")
    private LocalDate scheduleDate;

    @NotNull(message = "Session start time is required")
    private LocalTime sessionStartTime;

    @NotNull(message = "Session end time is required")
    private LocalTime sessionEndTime;

    private Integer slotDurationMinutes = 30;

    @Min(value = 1, message = "Max patients in session must be at least 1")
    @Max(value = 100, message = "Max patients in session cannot exceed 100")
    private Integer maxPatientsInSession = 15;

    public DentistScheduleDTO() {
    }

    public DentistScheduleDTO(Integer dentistId, LocalDate scheduleDate, LocalTime sessionStartTime, LocalTime sessionEndTime, Integer slotDurationMinutes) {
        this.dentistId = dentistId;
        this.scheduleDate = scheduleDate;
        this.sessionStartTime = sessionStartTime;
        this.sessionEndTime = sessionEndTime;
        this.slotDurationMinutes = slotDurationMinutes;
    }

    // Getters and Setters
    public Integer getScheduleId() { return scheduleId; }
    public void setScheduleId(Integer scheduleId) { this.scheduleId = scheduleId; }

    public Integer getDentistId() { return dentistId; }
    public void setDentistId(Integer dentistId) { this.dentistId = dentistId; }

    public String getDentistName() { return dentistName; }
    public void setDentistName(String dentistName) { this.dentistName = dentistName; }

    public LocalDate getScheduleDate() { return scheduleDate; }
    public void setScheduleDate(LocalDate scheduleDate) { this.scheduleDate = scheduleDate; }

    public LocalTime getSessionStartTime() { return sessionStartTime; }
    public void setSessionStartTime(LocalTime sessionStartTime) { this.sessionStartTime = sessionStartTime; }

    public LocalTime getSessionEndTime() { return sessionEndTime; }
    public void setSessionEndTime(LocalTime sessionEndTime) { this.sessionEndTime = sessionEndTime; }

    public Integer getSlotDurationMinutes() { return slotDurationMinutes; }
    public void setSlotDurationMinutes(Integer slotDurationMinutes) { this.slotDurationMinutes = slotDurationMinutes; }

    public Integer getMaxPatientsInSession() { return maxPatientsInSession; }
    public void setMaxPatientsInSession(Integer maxPatientsInSession) { this.maxPatientsInSession = maxPatientsInSession; }
}
