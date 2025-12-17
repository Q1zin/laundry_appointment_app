package com.laundry.booking.dto;

import com.laundry.booking.entity.Booking;
import com.laundry.booking.entity.Machine;
import com.laundry.booking.entity.Timeslot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleData {
    private List<Machine> machines;
    private List<Timeslot> timeslots;
    private List<Booking> bookings;
}
