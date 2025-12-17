package com.laundry.booking.controller;

import com.laundry.booking.dto.AdminBookingDto;
import com.laundry.booking.dto.BookingResult;
import com.laundry.booking.dto.DateRequest;
import com.laundry.booking.dto.MachineRequest;
import com.laundry.booking.dto.UserRequest;
import com.laundry.booking.entity.User;
import com.laundry.booking.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin Controller - Admin Panel UI
 * Endpoints:
 * - POST /api/admin/machines/block
 * - POST /api/admin/machines/unblock
 * - POST /api/admin/bookings/open
 * - POST /api/admin/bookings/close
 * - DELETE /api/admin/bookings/:bookingId
 * - GET /api/admin/bookings
 * - GET /api/admin/users
 * - POST /api/admin/users/block
 * - POST /api/admin/users/unblock
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;

    /**
     * POST /api/admin/machines/block
     * Body: { machineId: String }
     * Response: { result: boolean, message: String }
     */
    @PostMapping("/machines/block")
    public ResponseEntity<BookingResult> blockMachine(@RequestBody MachineRequest request) {
        BookingResult result = adminService.blockMachine(request.getMachineId());
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/admin/machines/unblock
     * Body: { machineId: String }
     * Response: { result: boolean, message: String }
     */
    @PostMapping("/machines/unblock")
    public ResponseEntity<BookingResult> unblockMachine(@RequestBody MachineRequest request) {
        BookingResult result = adminService.unblockMachine(request.getMachineId());
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/admin/bookings/open
     * Body: { date: Date }
     * Response: { result: boolean, message: String }
     */
    @PostMapping("/bookings/open")
    public ResponseEntity<BookingResult> openBooking(@RequestBody DateRequest request) {
        BookingResult result = adminService.openBooking(request.getDate());
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/admin/bookings/close
     * Body: { date: Date }
     * Response: { result: boolean, message: String }
     */
    @PostMapping("/bookings/close")
    public ResponseEntity<BookingResult> closeBooking(@RequestBody DateRequest request) {
        BookingResult result = adminService.closeBooking(request.getDate());
        return ResponseEntity.ok(result);
    }

    /**
     * DELETE /api/admin/bookings/:bookingId
     * Response: { result: boolean, message: String }
     */
    @DeleteMapping("/bookings/{bookingId}")
    public ResponseEntity<BookingResult> deleteBooking(@PathVariable String bookingId) {
        BookingResult result = adminService.deleteBooking(bookingId);
        return ResponseEntity.ok(result);
    }

    /**
     * GET /api/admin/bookings
     * Response: List<AdminBookingDto>
     */
    @GetMapping("/bookings")
    public ResponseEntity<List<AdminBookingDto>> getAllBookings() {
        List<AdminBookingDto> bookings = adminService.getAllBookingsWithDetails();
        return ResponseEntity.ok(bookings);
    }

    /**
     * GET /api/admin/users
     * Response: List<User>
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * POST /api/admin/users/block
     * Body: { userId: String }
     * Response: { result: boolean, message: String }
     */
    @PostMapping("/users/block")
    public ResponseEntity<BookingResult> blockUser(@RequestBody UserRequest request) {
        BookingResult result = adminService.blockUser(request.getUserId());
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/admin/users/unblock
     * Body: { userId: String }
     * Response: { result: boolean, message: String }
     */
    @PostMapping("/users/unblock")
    public ResponseEntity<BookingResult> unblockUser(@RequestBody UserRequest request) {
        BookingResult result = adminService.unblockUser(request.getUserId());
        return ResponseEntity.ok(result);
    }
}
