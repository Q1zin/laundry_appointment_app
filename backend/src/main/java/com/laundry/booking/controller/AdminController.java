package com.laundry.booking.controller;

import com.laundry.booking.dto.*;
import com.laundry.booking.entity.Machine;
import com.laundry.booking.entity.User;
import com.laundry.booking.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin Controller - Admin Panel UI
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;

    // ============= MACHINES =============

    /**
     * GET /api/admin/machines
     * Response: List<Machine>
     */
    @GetMapping("/machines")
    public ResponseEntity<List<Machine>> getAllMachines() {
        List<Machine> machines = adminService.getAllMachines();
        return ResponseEntity.ok(machines);
    }

    /**
     * POST /api/admin/machines
     * Body: { name: String }
     * Response: Machine
     */
    @PostMapping("/machines")
    public ResponseEntity<Machine> createMachine(@RequestBody CreateMachineRequest request) {
        Machine machine = adminService.createMachine(request.getName());
        return ResponseEntity.ok(machine);
    }

    /**
     * DELETE /api/admin/machines/:machineId
     * Response: { result: boolean, message: String }
     */
    @DeleteMapping("/machines/{machineId}")
    public ResponseEntity<BookingResult> deleteMachine(@PathVariable String machineId) {
        BookingResult result = adminService.deleteMachine(machineId);
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/admin/machines/block
     */
    @PostMapping("/machines/block")
    public ResponseEntity<BookingResult> blockMachine(@RequestBody MachineRequest request) {
        BookingResult result = adminService.blockMachine(request.getMachineId());
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/admin/machines/unblock
     */
    @PostMapping("/machines/unblock")
    public ResponseEntity<BookingResult> unblockMachine(@RequestBody MachineRequest request) {
        BookingResult result = adminService.unblockMachine(request.getMachineId());
        return ResponseEntity.ok(result);
    }

    // ============= SCHEDULES =============

    /**
     * GET /api/admin/schedules
     * Response: List<ScheduleDto>
     */
    @GetMapping("/schedules")
    public ResponseEntity<List<ScheduleDto>> getAllSchedules() {
        List<ScheduleDto> schedules = adminService.getAllSchedules();
        return ResponseEntity.ok(schedules);
    }

    /**
     * POST /api/admin/schedules
     * Body: { date: LocalDate, isOpen: boolean, machineIds: List<String> }
     * Response: ScheduleDto
     */
    @PostMapping("/schedules")
    public ResponseEntity<ScheduleDto> createOrUpdateSchedule(@RequestBody ScheduleRequest request) {
        ScheduleDto schedule = adminService.createOrUpdateSchedule(request);
        return ResponseEntity.ok(schedule);
    }

    /**
     * DELETE /api/admin/schedules/:scheduleId
     * Response: { result: boolean, message: String }
     */
    @DeleteMapping("/schedules/{scheduleId}")
    public ResponseEntity<BookingResult> deleteSchedule(@PathVariable String scheduleId) {
        BookingResult result = adminService.deleteSchedule(scheduleId);
        return ResponseEntity.ok(result);
    }

    // ============= BOOKINGS =============

    /**
     * GET /api/admin/bookings
     */
    @GetMapping("/bookings")
    public ResponseEntity<List<AdminBookingDto>> getAllBookings() {
        List<AdminBookingDto> bookings = adminService.getAllBookingsWithDetails();
        return ResponseEntity.ok(bookings);
    }

    /**
     * DELETE /api/admin/bookings/:bookingId
     */
    @DeleteMapping("/bookings/{bookingId}")
    public ResponseEntity<BookingResult> deleteBooking(@PathVariable String bookingId) {
        BookingResult result = adminService.deleteBooking(bookingId);
        return ResponseEntity.ok(result);
    }

    // ============= USERS =============

    /**
     * GET /api/admin/users
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * POST /api/admin/users/block
     */
    @PostMapping("/users/block")
    public ResponseEntity<BookingResult> blockUser(@RequestBody UserRequest request) {
        BookingResult result = adminService.blockUser(request.getUserId());
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/admin/users/unblock
     */
    @PostMapping("/users/unblock")
    public ResponseEntity<BookingResult> unblockUser(@RequestBody UserRequest request) {
        BookingResult result = adminService.unblockUser(request.getUserId());
        return ResponseEntity.ok(result);
    }
}
