package com.dentalclinic.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DTO for creating/updating Treatment Types and procedure prices.
 */
public class TreatmentTypeDTO {

    private Integer treatmentTypeId;

    @NotBlank(message = "Treatment name is required")
    private String treatmentName;

    private String description;

    @NotNull(message = "Base cost is required")
    @DecimalMin(value = "0.00", message = "Base cost cannot be negative")
    private BigDecimal baseCost;

    private Boolean isActive = true;

    public TreatmentTypeDTO() {
    }

    public TreatmentTypeDTO(Integer treatmentTypeId, String treatmentName, String description, BigDecimal baseCost, Boolean isActive) {
        this.treatmentTypeId = treatmentTypeId;
        this.treatmentName = treatmentName;
        this.description = description;
        this.baseCost = baseCost;
        this.isActive = isActive != null ? isActive : true;
    }

    // Getters and Setters
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
