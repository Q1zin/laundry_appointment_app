package com.laundry.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBookingDto {
    private String id;
    private String machineId;
    private String machineName;
    private String slotId;
    private LocalDateTime slotStartTime;
    private LocalDateTime slotEndTime;
    private String state;
    private LocalDateTime createdAt;
}

