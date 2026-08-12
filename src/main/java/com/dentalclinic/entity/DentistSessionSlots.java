package com.dentalclinic.entity;

import jakarta.persistence.*;
import java.time.LocalTime;

/**
 * JPA Entity representing an individual generated time slot for a Dentist Shift.
 * 
 * Layer: Domain / Persistence Layer
 * Table: DentistSessionSlots
 */
@Entity
@Table(name = "DentistSessionSlots", indexes = {
    @Index(name = "IDX_DentistSessionSlots_Schedule", columnList = "ScheduleID"),
    @Index(name = "IDX_DentistSessionSlots_Dentist", columnList = "DentistID")
})
public class DentistSessionSlots {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SlotID")
    private Integer slotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ScheduleID", nullable = false)
    private DentistSchedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DentistID", nullable = false)
    private Dentist dentist;

    @Column(name = "SlotNumber", nullable = false)
    private Integer slotNumber;

    @Column(name = "SlotStartTime", nullable = false)
    private LocalTime slotStartTime;

    @Column(name = "SlotEndTime", nullable = false)
    private LocalTime slotEndTime;

    @Column(name = "IsBooked", nullable = false)
    private Boolean isBooked = false;

    public DentistSessionSlots() {
    }

    public DentistSessionSlots(DentistSchedule schedule, Dentist dentist, Integer slotNumber, LocalTime slotStartTime, LocalTime slotEndTime) {
        this.schedule = schedule;
        this.dentist = dentist;
        this.slotNumber = slotNumber;
        this.slotStartTime = slotStartTime;
        this.slotEndTime = slotEndTime;
        this.isBooked = false;
    }

    // Getters & Setters
    public Integer getSlotId() { return slotId; }
    public void setSlotId(Integer slotId) { this.slotId = slotId; }

    public DentistSchedule getSchedule() { return schedule; }
    public void setSchedule(DentistSchedule schedule) { this.schedule = schedule; }

    public Dentist getDentist() { return dentist; }
    public void setDentist(Dentist dentist) { this.dentist = dentist; }

    public Integer getSlotNumber() { return slotNumber; }
    public void setSlotNumber(Integer slotNumber) { this.slotNumber = slotNumber; }

    public LocalTime getSlotStartTime() { return slotStartTime; }
    public void setSlotStartTime(LocalTime slotStartTime) { this.slotStartTime = slotStartTime; }

    public LocalTime getSlotEndTime() { return slotEndTime; }
    public void setSlotEndTime(LocalTime slotEndTime) { this.slotEndTime = slotEndTime; }

    public Boolean getIsBooked() { return isBooked; }
    public void setIsBooked(Boolean isBooked) { this.isBooked = isBooked; }
}
