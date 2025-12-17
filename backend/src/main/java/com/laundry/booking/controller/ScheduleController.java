package com.laundry.booking.controller;

import com.laundry.booking.dto.ScheduleData;
import com.laundry.booking.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Schedule Controller - Schedule UI
 * Endpoints:
 * - GET /api/schedule?date={date}&userId={userId}
 */
@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * GET /api/schedule?date={date}&userId={userId}
     * Response: { schedule: ScheduleData }
     */
    @GetMapping
    public ResponseEntity<ScheduleData> getSchedule(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @RequestParam String userId
    ) {
        ScheduleData schedule = scheduleService.getSchedule(date, userId);
        return ResponseEntity.ok(schedule);
    }
}
