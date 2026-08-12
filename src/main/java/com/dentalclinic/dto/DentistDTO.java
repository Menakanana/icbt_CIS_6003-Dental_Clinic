package com.dentalclinic.dto;

import java.math.BigDecimal;

/**
 * DTO for Dentist details.
 * Compatible with JSP EL and REST APIs.
 */
public class DentistDTO {

    private Integer dentistId;
    private String dentistName;
    private String specialization;
    private String contactNumber;
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
