package com.dentalclinic.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * JPA Entity representing a Dentist Shift Schedule.
 * 
 * Layer: Domain / Persistence Layer
 * Table: DentistSchedule
 */
@Entity
@Table(name = "DentistSchedule", indexes = {
    @Index(name = "IDX_DentistSchedule_DentistDate", columnList = "DentistID, ScheduleDate")
})
public class DentistSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ScheduleID")
    private Integer scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DentistID", nullable = false)
    private Dentist dentist;

    @Column(name = "ScheduleDate", nullable = false)
    private LocalDate scheduleDate;

    @Column(name = "SessionStartTime", nullable = false)
    private LocalTime sessionStartTime;

    @Column(name = "SessionEndTime", nullable = false)
    private LocalTime sessionEndTime;

    @Column(name = "SlotDurationMinutes", nullable = false)
    private Integer slotDurationMinutes = 30;

    @Column(name = "MaxPatientsInSession", nullable = false)
    private Integer maxPatientsInSession = 15;

    @Column(name = "IsActive", nullable = false)
    private Boolean isActive = true;

    public DentistSchedule() {
    }

    public DentistSchedule(Dentist dentist, LocalDate scheduleDate, LocalTime sessionStartTime, LocalTime sessionEndTime, Integer slotDurationMinutes, Integer maxPatientsInSession) {
        this.dentist = dentist;
        this.scheduleDate = scheduleDate;
        this.sessionStartTime = sessionStartTime;
        this.sessionEndTime = sessionEndTime;
        this.slotDurationMinutes = slotDurationMinutes;
        this.maxPatientsInSession = maxPatientsInSession;
        this.isActive = true;
    }

    // Getters & Setters
    public Integer getScheduleId() { return scheduleId; }
    public void setScheduleId(Integer scheduleId) { this.scheduleId = scheduleId; }

    public Dentist getDentist() { return dentist; }
    public void setDentist(Dentist dentist) { this.dentist = dentist; }

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

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
