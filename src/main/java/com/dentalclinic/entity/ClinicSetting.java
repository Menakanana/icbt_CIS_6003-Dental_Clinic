package com.dentalclinic.entity;

import jakarta.persistence.*;

/**
 * Domain entity representing system-wide Clinic Configuration Settings.
 * Stores key-value settings such as clinic_name, clinic_address, clinic_phone, and clinic_charge.
 * 
 * Layer: Data Layer (JPA Entity)
 */
@Entity
@Table(name = "clinic_settings")
public class ClinicSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "settingid")
    private Integer settingId;

    @Column(name = "setting_key", nullable = false, unique = true, length = 50)
    private String settingKey;

    @Column(name = "setting_value", nullable = false, length = 255)
    private String settingValue;

    @Column(name = "description", length = 255)
    private String description;

    public ClinicSetting() {
    }

    public ClinicSetting(String settingKey, String settingValue, String description) {
        this.settingKey = settingKey;
        this.settingValue = settingValue;
        this.description = description;
    }

    // Getters and Setters
    public Integer getSettingId() { return settingId; }
    public void setSettingId(Integer settingId) { this.settingId = settingId; }

    public String getSettingKey() { return settingKey; }
    public void setSettingKey(String settingKey) { this.settingKey = settingKey; }

    public String getSettingValue() { return settingValue; }
    public void setSettingValue(String settingValue) { this.settingValue = settingValue; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
