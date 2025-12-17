package com.laundry.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminBookingDto {
    private String id;
    private String userId;
    private String userName;
    private String userFullName;
    private String userRoom;
    private String machineId;
    private String machineName;
    private String slotId;
    private LocalDateTime slotStartTime;
    private LocalDateTime slotEndTime;
    private String state;
    private LocalDateTime createdAt;
}
