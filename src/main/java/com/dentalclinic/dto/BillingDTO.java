package com.dentalclinic.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate appointmentDate;
    private BigDecimal consultationFee;
    private BigDecimal clinicCharge = new BigDecimal("500.00");
    private BigDecimal treatmentBaseCost;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private LocalDateTime generatedTimestamp = LocalDateTime.now();
    private String billStage = "FINAL_SETTLED"; // INITIAL_DEPOSIT vs FINAL_SETTLED
    private String paymentMethod = "Cash";      // Cash, Credit/Debit Card, Bank Transfer
    private String medicalHistory;
    private String patientEmail;
    private BigDecimal previousPaidAmount = BigDecimal.ZERO;
    private BigDecimal netBalanceDue = BigDecimal.ZERO;
    private java.util.List<TreatmentItemDTO> treatmentItems = new java.util.ArrayList<>();

    public static class TreatmentItemDTO {
        private String name;
        private BigDecimal cost;
        public TreatmentItemDTO() {}
        public TreatmentItemDTO(String name, BigDecimal cost) { this.name = name; this.cost = cost; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public BigDecimal getCost() { return cost; }
        public void setCost(BigDecimal cost) { this.cost = cost; }
    }

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

    public String getBillStage() { return billStage; }
    public void setBillStage(String billStage) { this.billStage = billStage; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }

    public String getPatientEmail() { return patientEmail; }
    public void setPatientEmail(String patientEmail) { this.patientEmail = patientEmail; }

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
    public String getFormattedAppointmentDate() {
        return appointmentDate != null ? appointmentDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }

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

    public BigDecimal getPreviousPaidAmount() { return previousPaidAmount != null ? previousPaidAmount : BigDecimal.ZERO; }
    public void setPreviousPaidAmount(BigDecimal previousPaidAmount) { this.previousPaidAmount = previousPaidAmount; }

    public BigDecimal getNetBalanceDue() { return netBalanceDue != null ? netBalanceDue : BigDecimal.ZERO; }
    public void setNetBalanceDue(BigDecimal netBalanceDue) { this.netBalanceDue = netBalanceDue; }

    public java.util.List<TreatmentItemDTO> getTreatmentItems() { return treatmentItems; }
    public void setTreatmentItems(java.util.List<TreatmentItemDTO> treatmentItems) { this.treatmentItems = treatmentItems; }

    public LocalDateTime getGeneratedTimestamp() { return generatedTimestamp; }
    public void setGeneratedTimestamp(LocalDateTime generatedTimestamp) { this.generatedTimestamp = generatedTimestamp; }
}
