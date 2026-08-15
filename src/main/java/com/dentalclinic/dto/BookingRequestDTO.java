package com.dentalclinic.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for Appointment Booking Requests.
 * Supports both Existing Patient Selection AND 1-Step Inline Quick-Add Patient Registration.
 */
public class BookingRequestDTO {

    private Integer patientId;

    // Inline Quick-Add Patient fields (for 1-step registration during booking)
    private String quickPatientName;
    private String quickContactNumber;
    private String quickNic;

    @NotNull(message = "Dentist ID is required")
    private Integer dentistId;

    private Integer treatmentTypeId;

    @NotNull(message = "Appointment date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate appointmentDate;

    @NotNull(message = "Start time is required")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    private Integer tokenNumber;
    private String notes;

    public BookingRequestDTO() {
    }

    // Getters & Setters
    public Integer getPatientId() { return patientId; }
    public void setPatientId(Integer patientId) { this.patientId = patientId; }

    public String getQuickPatientName() { return quickPatientName; }
    public void setQuickPatientName(String quickPatientName) { this.quickPatientName = quickPatientName; }

    public String getQuickContactNumber() { return quickContactNumber; }
    public void setQuickContactNumber(String quickContactNumber) { this.quickContactNumber = quickContactNumber; }

    public String getQuickNic() { return quickNic; }
    public void setQuickNic(String quickNic) { this.quickNic = quickNic; }

    public Integer getDentistId() { return dentistId; }
    public void setDentistId(Integer dentistId) { this.dentistId = dentistId; }

    public Integer getTreatmentTypeId() { return treatmentTypeId; }
    public void setTreatmentTypeId(Integer treatmentTypeId) { this.treatmentTypeId = treatmentTypeId; }

    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Integer getTokenNumber() { return tokenNumber; }
    public void setTokenNumber(Integer tokenNumber) { this.tokenNumber = tokenNumber; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Integer patientId() { return patientId; }
    public Integer dentistId() { return dentistId; }
    public LocalDate appointmentDate() { return appointmentDate; }
    public LocalTime startTime() { return startTime; }
}
