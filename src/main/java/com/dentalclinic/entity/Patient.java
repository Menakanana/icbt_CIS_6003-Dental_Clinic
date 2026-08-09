package com.dentalclinic.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity class representing a Dental Clinic Patient.
 * 
 * Layer: Domain / Entity Layer
 * Role: Maps directly to the "Patients" database table using Jakarta Persistence (JPA).
 */
@Entity
@Table(
    name = "Patients",
    indexes = {
        @Index(name = "IDX_Patients_ContactNumber", columnList = "ContactNumber"),
        @Index(name = "IDX_Patients_NIC", columnList = "NIC"),
        @Index(name = "IDX_Patients_IsActive", columnList = "IsActive")
    }
)
public class Patient {

    /** Primary Key - Auto-incremented Patient ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PatientID")
    private Integer patientId;

    /** Patient's full name */
    @Column(name = "PatientName", nullable = false, length = 100)
    private String patientName;

    /** Residential address */
    @Column(name = "Address", length = 255)
    private String address;

    /** Primary contact phone number */
    @Column(name = "ContactNumber", nullable = false, length = 15)
    private String contactNumber;

    /** Optional email address */
    @Column(name = "Email", length = 100)
    private String email;

    /** Date of Birth */
    @Column(name = "DateOfBirth")
    private LocalDate dateOfBirth;

    /** Gender ('M', 'F', 'O') */
    @Column(name = "Gender", length = 1)
    private String gender;

    /** National Identity Card (NIC) number */
    @Column(name = "NIC", length = 20)
    private String nic;

    /** Date and time when the patient was registered */
    @Column(name = "RegisteredDate")
    private LocalDateTime registeredDate;

    /** Active status flag (true = active, false = soft-deleted) */
    @Column(name = "IsActive", nullable = false)
    private Boolean isActive = true;

    /** Default constructor initializing the registration date */
    public Patient() {
        this.registeredDate = LocalDateTime.now();
    }

    /** Parametrized constructor for easy instantiation */
    public Patient(String patientName, String contactNumber, String email, String address, String nic) {
        this();
        this.patientName = patientName;
        this.contactNumber = contactNumber;
        this.email = email;
        this.address = address;
        this.nic = nic;
    }

    // ==========================================
    // Getters and Setters
    // ==========================================

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public LocalDateTime getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(LocalDateTime registeredDate) {
        this.registeredDate = registeredDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
