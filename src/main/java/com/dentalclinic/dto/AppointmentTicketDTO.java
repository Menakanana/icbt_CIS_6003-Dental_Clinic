package com.dentalclinic.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO representing a generated Appointment Ticket (Combining Time Window & Queue Token Number).
 */
public class AppointmentTicketDTO {

    private Integer appointmentId;
    private Integer appointmentNumber;
    private Integer tokenNumber;
    private Integer patientId;
    private String patientName;
    private String contactNumber;
    private String patientEmail;
    private String nic;
    private Integer dentistId;
    private String dentistName;
    private String specialization;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate appointmentDate;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    private String displayTimeRange;
    private String status;
    private BigDecimal consultationFee;
    private String treatmentName;
    private BigDecimal treatmentBaseCost;
    private String notes;

    public AppointmentTicketDTO() {
    }

    public AppointmentTicketDTO(Integer appointmentId, Integer appointmentNumber, Integer tokenNumber, Integer patientId, String patientName, String contactNumber, String nic, Integer dentistId, String dentistName, String specialization, LocalDate appointmentDate, LocalTime startTime, LocalTime endTime, String displayTimeRange, String status, BigDecimal consultationFee) {
        this.appointmentId = appointmentId;
        this.appointmentNumber = appointmentNumber;
        this.tokenNumber = tokenNumber;
        this.patientId = patientId;
        this.patientName = patientName;
        this.contactNumber = contactNumber;
        this.nic = nic;
        this.dentistId = dentistId;
        this.dentistName = dentistName;
        this.specialization = specialization;
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.displayTimeRange = displayTimeRange;
        this.status = status;
        this.consultationFee = consultationFee;
    }

    // Getters & Setters
    public Integer getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Integer appointmentId) { this.appointmentId = appointmentId; }

    public Integer getAppointmentNumber() { return appointmentNumber; }
    public void setAppointmentNumber(Integer appointmentNumber) { this.appointmentNumber = appointmentNumber; }

    public Integer getTokenNumber() { return tokenNumber; }
    public void setTokenNumber(Integer tokenNumber) { this.tokenNumber = tokenNumber; }

    public Integer getPatientId() { return patientId; }
    public void setPatientId(Integer patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getPatientEmail() { return patientEmail; }
    public void setPatientEmail(String patientEmail) { this.patientEmail = patientEmail; }

    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    public Integer getDentistId() { return dentistId; }
    public void setDentistId(Integer dentistId) { this.dentistId = dentistId; }

    public String getDentistName() { return dentistName; }
    public void setDentistName(String dentistName) { this.dentistName = dentistName; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }
    public String getFormattedAppointmentDate() {
        return appointmentDate != null ? appointmentDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public String getDisplayTimeRange() { return displayTimeRange; }
    public void setDisplayTimeRange(String displayTimeRange) { this.displayTimeRange = displayTimeRange; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getConsultationFee() { return consultationFee; }
    public void setConsultationFee(BigDecimal consultationFee) { this.consultationFee = consultationFee; }

    public String getTreatmentName() { return treatmentName; }
    public void setTreatmentName(String treatmentName) { this.treatmentName = treatmentName; }

    public BigDecimal getTreatmentBaseCost() { return treatmentBaseCost; }
    public void setTreatmentBaseCost(BigDecimal treatmentBaseCost) { this.treatmentBaseCost = treatmentBaseCost; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }

    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public BigDecimal getPaidAmount() { return paidAmount != null ? paidAmount : BigDecimal.ZERO; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    private String medicalHistory;
    private String relationship;
    private String paymentStatus = "UNPAID";
    private BigDecimal paidAmount = BigDecimal.ZERO;
    private String paymentMethod = "Cash";

    public Integer tokenNumber() { return tokenNumber; }
    public String patientName() { return patientName; }
    public String dentistName() { return dentistName; }
}
