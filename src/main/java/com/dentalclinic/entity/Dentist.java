package com.dentalclinic.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * JPA Entity representing a Dentist profile.
 * 
 * Layer: Domain / Persistence Layer
 * Table: Dentists
 */
@Entity
@Table(name = "Dentists", indexes = {
    @Index(name = "IDX_Dentists_IsActive", columnList = "IsActive")
})
public class Dentist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DentistID")
    private Integer dentistId;

    @Column(name = "DentistName", nullable = false, length = 100)
    private String dentistName;

    @Column(name = "Email", length = 100)
    private String email;

    @Column(name = "ContactNumber", length = 15)
    private String contactNumber;

    @Column(name = "Specialization", length = 100)
    private String specialization;

    @Column(name = "LicenseNumber", length = 50)
    private String licenseNumber;

    @Column(name = "Qualifications", length = 255)
    private String qualifications;

    @Column(name = "ConsultationFee", nullable = false, precision = 10, scale = 2)
    private BigDecimal consultationFee = new BigDecimal("1500.00");

    @Column(name = "ExperienceYears")
    private Integer experienceYears;

    @Column(name = "IsActive", nullable = false)
    private Boolean isActive = true;

    public Dentist() {
    }

    public Dentist(String dentistName, String specialization, String contactNumber, BigDecimal consultationFee) {
        this.dentistName = dentistName;
        this.specialization = specialization;
        this.contactNumber = contactNumber;
        this.consultationFee = consultationFee;
        this.isActive = true;
    }

    // Getters & Setters
    public Integer getDentistId() { return dentistId; }
    public void setDentistId(Integer dentistId) { this.dentistId = dentistId; }

    public String getDentistName() { return dentistName; }
    public void setDentistName(String dentistName) { this.dentistName = dentistName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getQualifications() { return qualifications; }
    public void setQualifications(String qualifications) { this.qualifications = qualifications; }

    public BigDecimal getConsultationFee() { return consultationFee; }
    public void setConsultationFee(BigDecimal consultationFee) { this.consultationFee = consultationFee; }

    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
