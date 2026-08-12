package com.dentalclinic.dto;

import java.time.LocalTime;

/**
 * DTO representing an individual dynamic time slot with Token Number and Availability status.
 */
public class SlotDTO {

    private Integer slotId;
    private Integer tokenNumber;
    private LocalTime startTime;
    private LocalTime endTime;
    private String displayTime;
    private Boolean isAvailable;

    public SlotDTO() {
    }

    public SlotDTO(Integer slotId, Integer tokenNumber, LocalTime startTime, LocalTime endTime, String displayTime, Boolean isAvailable) {
        this.slotId = slotId;
        this.tokenNumber = tokenNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.displayTime = displayTime;
        this.isAvailable = isAvailable;
    }

    public Integer getSlotId() { return slotId; }
    public void setSlotId(Integer slotId) { this.slotId = slotId; }

    public Integer getTokenNumber() { return tokenNumber; }
    public void setTokenNumber(Integer tokenNumber) { this.tokenNumber = tokenNumber; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public String getDisplayTime() { return displayTime; }
    public void setDisplayTime(String displayTime) { this.displayTime = displayTime; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }

    public Integer slotId() { return slotId; }
    public Integer tokenNumber() { return tokenNumber; }
    public LocalTime startTime() { return startTime; }
    public LocalTime endTime() { return endTime; }
    public String displayTime() { return displayTime; }
    public Boolean isAvailable() { return isAvailable; }
}
