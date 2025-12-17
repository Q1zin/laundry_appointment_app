package com.laundry.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RescheduleBookingRequest {
    private String bookingId;
    private String newSlotId;
    private String userId;
}
