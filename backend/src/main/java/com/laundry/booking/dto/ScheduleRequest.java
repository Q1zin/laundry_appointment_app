package com.laundry.booking.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class ScheduleRequest {
    private LocalDate date;
    private Boolean isOpen;
    private List<String> machineIds; // машинки, для которых открыта запись на эту дату
    private List<String> timeSlots; // временные слоты в формате "HH:mm-HH:mm" (например ["08:00-10:00", "10:00-12:00"])
}
