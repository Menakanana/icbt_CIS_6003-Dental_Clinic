package com.dentalclinic.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity class representing a system User (Admin, Receptionist, Dentist, etc.).
 * 
 * Layer: Domain / Entity Layer
 * Role: Maps directly to the "Users" table in the relational database via Jakarta Persistence (JPA).
 * Security: Password field stores BCrypt hashed strings, NOT raw plain text passwords.
 */
@Entity
@Table(
    name = "Users",
    uniqueConstraints = {
        @UniqueConstraint(name = "UQ_Users_Username", columnNames = {"Username"})
    },
    indexes = {
        @Index(name = "IDX_Users_Role", columnList = "Role"),
        @Index(name = "IDX_Users_Username", columnList = "Username")
    }
)
public class User {

    /** Primary key - Auto-incremented User ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private Integer userId;

    /** Unique login username */
    @Column(name = "Username", nullable = false, unique = true, length = 50)
    private String username;

    /** One-way BCrypt hashed password */
    @Column(name = "Password", nullable = false, length = 255)
    private String password;

    /** User email address */
    @Column(name = "Email", length = 100)
    private String email;

    /** Full display name of the user */
    @Column(name = "FullName", nullable = false, length = 100)
    private String fullName;

    /** System access role (e.g., "Admin", "Receptionist", "Dentist") */
    @Column(name = "Role", nullable = false, length = 50)
    private String role = "Receptionist";

    /** Account active flag (true = active, false = disabled) */
    @Column(name = "IsActive", nullable = false)
    private Boolean isActive = true;

    /** Account creation timestamp */
    @Column(name = "CreatedDate")
    private LocalDateTime createdDate;

    /** Timestamp of the user's last successful login */
    @Column(name = "LastLogin")
    private LocalDateTime lastLogin;

    /** ID of the administrator who created this user */
    @Column(name = "CreatedBy")
    private Integer createdBy;

    /** Default no-arg constructor required by JPA specification */
    public User() {
        this.createdDate = LocalDateTime.now();
    }

    /** Convenience constructor for creating a new user instance */
    public User(String username, String password, String fullName, String role, String email) {
        this();
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.email = email;
    }

    // ==========================================
    // Getters and Setters
    // ==========================================

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }
}
