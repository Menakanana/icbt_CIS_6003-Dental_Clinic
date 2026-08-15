package com.dentalclinic.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * JPA Entity representing a Treatment / Dental Procedure catalog item.
 * 
 * Layer: Domain / Persistence Layer
 * Table: TreatmentTypes
 */
@Entity
@Table(name = "TreatmentTypes", indexes = {
    @Index(name = "IDX_TreatmentTypes_IsActive", columnList = "IsActive")
})
public class TreatmentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TreatmentTypeID")
    private Integer treatmentTypeId;

    @Column(name = "TreatmentName", nullable = false, length = 100)
    private String treatmentName;

    @Column(name = "Description", length = 255)
    private String description;

    @Column(name = "BaseCost", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseCost;

    @Column(name = "IsActive", nullable = false)
    private Boolean isActive = true;

    public TreatmentType() {
    }

    public TreatmentType(String treatmentName, String description, BigDecimal baseCost) {
        this.treatmentName = treatmentName;
        this.description = description;
        this.baseCost = baseCost;
        this.isActive = true;
    }

    // Getters & Setters
    public Integer getTreatmentTypeId() { return treatmentTypeId; }
    public void setTreatmentTypeId(Integer treatmentTypeId) { this.treatmentTypeId = treatmentTypeId; }

    public String getTreatmentName() { return treatmentName; }
    public void setTreatmentName(String treatmentName) { this.treatmentName = treatmentName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getBaseCost() { return baseCost; }
    public void setBaseCost(BigDecimal baseCost) { this.baseCost = baseCost; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
