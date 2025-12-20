package com.laundry.booking.controller;

import com.laundry.booking.dto.*;
import com.laundry.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Booking Controller - Booking UI
 * Endpoints:
 * - POST /api/bookings/create
 * - POST /api/bookings/cancel
 * - POST /api/bookings/reschedule
 * - GET /api/bookings/can-book/:userId
 * - GET /api/bookings/user/:userId
 * - GET /api/slots/available/:machineId/:slotId
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;

    /**
     * POST /api/bookings/create
     * Body: { userId: String, machineId: String, slotId: String }
     * Response: { result: boolean, message: String }
     */
    @PostMapping("/create")
    public ResponseEntity<BookingResult> createBooking(@RequestBody CreateBookingRequest request) {
        BookingResult result = bookingService.createBooking(
            request.getUserId(),
            request.getMachineId(),
            request.getSlotId()
        );
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/bookings/cancel
     * Body: { bookingId: String, userId: String }
     * Response: { result: boolean, message: String }
     */
    @PostMapping("/cancel")
    public ResponseEntity<BookingResult> cancelBooking(@RequestBody CancelBookingRequest request) {
        BookingResult result = bookingService.cancelBooking(
            request.getBookingId(),
            request.getUserId()
        );
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/bookings/reschedule
     * Body: { bookingId: String, newSlotId: String, userId: String }
     * Response: { result: boolean, message: String }
     */
    @PostMapping("/reschedule")
    public ResponseEntity<BookingResult> rescheduleBooking(@RequestBody RescheduleBookingRequest request) {
        BookingResult result = bookingService.rescheduleBooking(
            request.getBookingId(),
            request.getNewSlotId(),
            request.getUserId()
        );
        return ResponseEntity.ok(result);
    }

    /**
     * GET /api/bookings/can-book/:userId
     * Response: { canBook: boolean }
     */
    @GetMapping("/can-book/{userId}")
    public ResponseEntity<Boolean> canUserBook(@PathVariable String userId) {
        boolean canBook = bookingService.canUserBook(userId);
        return ResponseEntity.ok(canBook);
    }

    /**
     * GET /api/bookings/user/:userId
     * Response: List<UserBookingDto>
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserBookingDto>> getUserBookings(@PathVariable String userId) {
        List<UserBookingDto> bookings = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    /**
     * GET /api/slots/available/:machineId/:slotId
     * Response: { available: boolean }
     */
    @GetMapping("/slots/available/{machineId}/{slotId}")
    public ResponseEntity<Boolean> isSlotAvailable(
        @PathVariable String machineId,
        @PathVariable String slotId
    ) {
        boolean available = bookingService.isSlotAvailable(machineId, slotId);
        return ResponseEntity.ok(available);
    }
}
