package com.dentalclinic.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * JPA Entity representing a Patient Appointment Booking.
 * Includes Patient, Dentist, TreatmentType, Schedule, Slot, TokenNumber, Date, Status, and Time Range.
 * 
 * Layer: Domain / Persistence Layer
 * Table: Appointments
 */
@Entity
@Table(name = "Appointments", indexes = {
    @Index(name = "IDX_Appointments_PatientID", columnList = "PatientID"),
    @Index(name = "IDX_Appointments_DentistID", columnList = "DentistID"),
    @Index(name = "IDX_Appointments_Date", columnList = "AppointmentDate"),
    @Index(name = "IDX_Appointments_Status", columnList = "Status")
})
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AppointmentID")
    private Integer appointmentId;

    @Column(name = "AppointmentNumber", insertable = false, updatable = false)
    private Integer appointmentNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PatientID", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DentistID", nullable = false)
    private Dentist dentist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TreatmentTypeID")
    private TreatmentType treatmentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ScheduleID")
    private DentistSchedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SlotID")
    private DentistSessionSlots slot;

    @Column(name = "TokenNumber", nullable = false)
    private Integer tokenNumber = 1;

    @Column(name = "AppointmentDate", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "StartTime", nullable = false)
    private LocalTime startTime;

    @Column(name = "EndTime", nullable = false)
    private LocalTime endTime;

    @Column(name = "Status", nullable = false, length = 20)
    private String status = "BOOKED";

    @Column(name = "Notes", length = 255)
    private String notes;

    public Appointment() {
    }

    public Appointment(Patient patient, Dentist dentist, TreatmentType treatmentType, DentistSchedule schedule, DentistSessionSlots slot, Integer tokenNumber, LocalDate appointmentDate, LocalTime startTime, LocalTime endTime, String status) {
        this.patient = patient;
        this.dentist = dentist;
        this.treatmentType = treatmentType;
        this.schedule = schedule;
        this.slot = slot;
        this.tokenNumber = tokenNumber;
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    // Getters & Setters
    public Integer getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Integer appointmentId) { this.appointmentId = appointmentId; }

    public Integer getAppointmentNumber() { return appointmentNumber; }
    public void setAppointmentNumber(Integer appointmentNumber) { this.appointmentNumber = appointmentNumber; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Dentist getDentist() { return dentist; }
    public void setDentist(Dentist dentist) { this.dentist = dentist; }

    public TreatmentType getTreatmentType() { return treatmentType; }
    public void setTreatmentType(TreatmentType treatmentType) { this.treatmentType = treatmentType; }

    public DentistSchedule getSchedule() { return schedule; }
    public void setSchedule(DentistSchedule schedule) { this.schedule = schedule; }

    public DentistSessionSlots getSlot() { return slot; }
    public void setSlot(DentistSessionSlots slot) { this.slot = slot; }

    public Integer getTokenNumber() { return tokenNumber; }
    public void setTokenNumber(Integer tokenNumber) { this.tokenNumber = tokenNumber; }

    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
