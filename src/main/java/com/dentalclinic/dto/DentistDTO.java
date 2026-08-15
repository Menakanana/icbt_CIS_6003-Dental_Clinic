package com.dentalclinic.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DTO for Dentist details.
 * Compatible with JSP EL and REST APIs.
 */
public class DentistDTO {

    private Integer dentistId;

    @NotBlank(message = "Doctor name is required")
    private String dentistName;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    private String contactNumber;

    @NotNull(message = "Consultation fee is required")
    @DecimalMin(value = "0.01", message = "Consultation fee must be greater than 0.00")
    private BigDecimal consultationFee;

    public DentistDTO() {
    }

    public DentistDTO(Integer dentistId, String dentistName, String specialization, String contactNumber, BigDecimal consultationFee) {
        this.dentistId = dentistId;
        this.dentistName = dentistName;
        this.specialization = specialization;
        this.contactNumber = contactNumber;
        this.consultationFee = consultationFee;
    }

    public Integer getDentistId() { return dentistId; }
    public void setDentistId(Integer dentistId) { this.dentistId = dentistId; }

    public String getDentistName() { return dentistName; }
    public void setDentistName(String dentistName) { this.dentistName = dentistName; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public BigDecimal getConsultationFee() { return consultationFee; }
    public void setConsultationFee(BigDecimal consultationFee) { this.consultationFee = consultationFee; }

    public Integer dentistId() { return dentistId; }
    public String dentistName() { return dentistName; }
    public String specialization() { return specialization; }
    public String contactNumber() { return contactNumber; }
    public BigDecimal consultationFee() { return consultationFee; }
}
