package com.dentalclinic.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Patient Billing & Invoice Receipts.
 * Includes breakdown of Consultation Fee, Treatment Cost, Discount, and Net Bill Total.
 */
public class BillingDTO {

    private Integer invoiceNumber;
    private Integer appointmentId;
    private Integer tokenNumber;
    private String patientName;
    private String patientContact;
    private String dentistName;
    private String dentistSpecialization;
    private String treatmentName;
    private LocalDate appointmentDate;
    private BigDecimal consultationFee;
    private BigDecimal clinicCharge = new BigDecimal("500.00");
    private BigDecimal treatmentBaseCost;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private LocalDateTime generatedTimestamp;

    public BillingDTO() {
    }

    public BillingDTO(Integer invoiceNumber, Integer appointmentId, Integer tokenNumber,
                      String patientName, String patientContact, String dentistName,
                      String dentistSpecialization, String treatmentName, LocalDate appointmentDate,
                      BigDecimal consultationFee, BigDecimal clinicCharge, BigDecimal treatmentBaseCost,
                      BigDecimal discountAmount, BigDecimal totalAmount) {
        this.invoiceNumber = invoiceNumber;
        this.appointmentId = appointmentId;
        this.tokenNumber = tokenNumber;
        this.patientName = patientName;
        this.patientContact = patientContact;
        this.dentistName = dentistName;
        this.dentistSpecialization = dentistSpecialization;
        this.treatmentName = treatmentName;
        this.appointmentDate = appointmentDate;
        this.consultationFee = consultationFee;
        this.clinicCharge = clinicCharge != null ? clinicCharge : new BigDecimal("500.00");
        this.treatmentBaseCost = treatmentBaseCost;
        this.discountAmount = discountAmount;
        this.totalAmount = totalAmount;
        this.generatedTimestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(Integer invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public Integer getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Integer appointmentId) { this.appointmentId = appointmentId; }

    public Integer getTokenNumber() { return tokenNumber; }
    public void setTokenNumber(Integer tokenNumber) { this.tokenNumber = tokenNumber; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getPatientContact() { return patientContact; }
    public void setPatientContact(String patientContact) { this.patientContact = patientContact; }

    public String getDentistName() { return dentistName; }
    public void setDentistName(String dentistName) { this.dentistName = dentistName; }

    public String getDentistSpecialization() { return dentistSpecialization; }
    public void setDentistSpecialization(String dentistSpecialization) { this.dentistSpecialization = dentistSpecialization; }

    public String getTreatmentName() { return treatmentName; }
    public void setTreatmentName(String treatmentName) { this.treatmentName = treatmentName; }

    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }

    public BigDecimal getConsultationFee() { return consultationFee; }
    public void setConsultationFee(BigDecimal consultationFee) { this.consultationFee = consultationFee; }

    public BigDecimal getClinicCharge() { return clinicCharge; }
    public void setClinicCharge(BigDecimal clinicCharge) { this.clinicCharge = clinicCharge; }

    public BigDecimal getTreatmentBaseCost() { return treatmentBaseCost; }
    public void setTreatmentBaseCost(BigDecimal treatmentBaseCost) { this.treatmentBaseCost = treatmentBaseCost; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getGeneratedTimestamp() { return generatedTimestamp; }
    public void setGeneratedTimestamp(LocalDateTime generatedTimestamp) { this.generatedTimestamp = generatedTimestamp; }
}
