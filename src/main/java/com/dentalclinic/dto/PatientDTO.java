package com.dentalclinic.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) for Patient Data Transfers.
 * Includes Level 2 Advanced Validations (Sri Lankan NIC regex, SL Phone Number regex, Email, Past DOB).
 * Compatible with Spring MVC Form Binding and REST APIs.
 */
public class PatientDTO {

    private Integer patientId;

    @NotBlank(message = "Patient name is required")
    @Size(min = 2, max = 100, message = "Patient name must be between 2 and 100 characters")
    private String patientName;

    @NotBlank(message = "Contact number is required")
    @Pattern(
        regexp = "^(?:0|\\+94)?7[0-9]{8}$",
        message = "Invalid Sri Lankan contact number format (e.g. 0771234567 or +94771234567)"
    )
    private String contactNumber;

    @Email(message = "Invalid email address format")
    private String email;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;

    @Pattern(
        regexp = "^$|^([0-9]{9}[vVxX]|[0-9]{12})$",
        message = "Invalid Sri Lankan NIC format (must be 9 digits ending in V/X or 12 digits)"
    )
    private String nic;

    @Past(message = "Date of Birth must be a date in the past")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^[MFO]$", message = "Gender must be 'M', 'F', or 'O'")
    private String gender;

    private String relationship = "Self";
    private String medicalHistory;

    public PatientDTO() {
    }

    public PatientDTO(Integer patientId, String patientName, String contactNumber, String email, String address, String nic) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.contactNumber = contactNumber;
        this.email = email;
        this.address = address;
        this.nic = nic;
    }

    public PatientDTO(Integer patientId, String patientName, String contactNumber, String email, String address, String nic, LocalDate dateOfBirth, String gender) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.contactNumber = contactNumber;
        this.email = email;
        this.address = address;
        this.nic = nic;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }

    public PatientDTO(Integer patientId, String patientName, String contactNumber, String email, String address, String nic, LocalDate dateOfBirth, String gender, String relationship, String medicalHistory) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.contactNumber = contactNumber;
        this.email = email;
        this.address = address;
        this.nic = nic;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.relationship = relationship;
        this.medicalHistory = medicalHistory;
    }

    // Standard JavaBeans Getters & Setters
    public Integer getPatientId() { return patientId; }
    public void setPatientId(Integer patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }

    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }

    // Record-style accessor aliases
    public Integer patientId() { return patientId; }
    public String patientName() { return patientName; }
    public String contactNumber() { return contactNumber; }
    public String email() { return email; }
    public String address() { return address; }
    public String nic() { return nic; }
    public LocalDate dateOfBirth() { return dateOfBirth; }
    public String gender() { return gender; }
    public String relationship() { return relationship; }
    public String medicalHistory() { return medicalHistory; }
}
