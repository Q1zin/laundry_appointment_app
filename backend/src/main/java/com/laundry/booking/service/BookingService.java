package com.laundry.booking.service;

import com.laundry.booking.dto.BookingResult;
import com.laundry.booking.entity.Booking;
import com.laundry.booking.entity.Timeslot;
import com.laundry.booking.entity.User;
import com.laundry.booking.repository.BookingRepository;
import com.laundry.booking.repository.TimeslotRepository;
import com.laundry.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TimeslotRepository timeslotRepository;
    private final UserRepository userRepository;

    /**
     * Booking Controller - createBooking method
     * Последовательность вызовов:
     * 1. Проверить права пользователя
     * 2. Проверить доступность слота
     * 3. Создать бронирование
     * 4. Вернуть результат
     */
    @Transactional
    public BookingResult createBooking(String userId, String machineId, String slotId) {
        // Шаг 1.1: Проверка прав пользователя
        if (!canUserBook(userId)) {
            return new BookingResult(false, "User cannot book");
        }

        // Шаг 1.2: Проверка доступности слота
        if (!isSlotAvailable(machineId, slotId)) {
            return new BookingResult(false, "Slot not available");
        }

        // Шаг 2: Создание бронирования
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setMachineId(machineId);
        booking.setSlotId(slotId);
        booking.setState("active");
        
        bookingRepository.save(booking);

        // Зарезервировать слот
        Timeslot slot = timeslotRepository.findById(slotId).orElse(null);
        if (slot != null) {
            slot.reserve();
            timeslotRepository.save(slot);
        }

        return new BookingResult(true, "Booking created successfully");
    }

    /**
     * Booking Controller - cancelBooking method
     * Последовательность вызовов:
     * 1. Проверить права на отмену
     * 2. Установить состояние "canceled"
     * 3. Освободить слот
     * 4. Вернуть результат
     */
    @Transactional
    public BookingResult cancelBooking(String bookingId, String userId) {
        // Шаг 1: Проверить права на отмену
        if (!canCancel(bookingId, userId)) {
            return new BookingResult(false, "Cannot cancel this booking");
        }

        // Шаг 2: Найти и отменить бронирование
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            return new BookingResult(false, "Booking not found");
        }

        booking.setState("canceled");
        bookingRepository.save(booking);

        // Шаг 3: Освободить слот
        Timeslot slot = timeslotRepository.findById(booking.getSlotId()).orElse(null);
        if (slot != null) {
            slot.free();
            timeslotRepository.save(slot);
        }

        return new BookingResult(true, "Booking canceled successfully");
    }

    /**
     * Booking Controller - rescheduleBooking method
     * Последовательность вызовов:
     * 1. Проверить возможность переноса
     * 2. Загрузить бронирование
     * 3. Получить старый слот
     * 4. Освободить старый слот
     * 5. Зарезервировать новый слот
     * 6. Установить новый слот
     * 7. Вернуть результат
     */
    @Transactional
    public BookingResult rescheduleBooking(String bookingId, String newSlotId, String userId) {
        // Шаг 1: Проверить возможность переноса
        if (!canReschedule(bookingId, newSlotId, userId)) {
            return new BookingResult(false, "Cannot reschedule this booking");
        }

        // Шаг 2: Загрузить бронирование
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            return new BookingResult(false, "Booking not found");
        }

        // Шаг 3: Получить ID старого слота
        String oldSlotId = booking.getSlotId();

        // Шаг 4: Освободить старый слот
        Timeslot oldSlot = timeslotRepository.findById(oldSlotId).orElse(null);
        if (oldSlot != null) {
            oldSlot.free();
            timeslotRepository.save(oldSlot);
        }

        // Шаг 5: Зарезервировать новый слот
        Timeslot newSlot = timeslotRepository.findById(newSlotId).orElse(null);
        if (newSlot != null) {
            newSlot.reserve();
            timeslotRepository.save(newSlot);
        }

        // Шаг 6: Установить новый слот
        booking.setSlot(newSlotId);
        bookingRepository.save(booking);

        return new BookingResult(true, "Booking rescheduled successfully");
    }

    /**
     * Проверка прав пользователя на бронирование
     */
    public boolean canUserBook(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        
        // Проверить, не заблокирован ли пользователь
        if (user.isBlocked()) {
            return false;
        }

        // Проверить количество активных бронирований
        long activeBookings = bookingRepository.findByUserIdAndState(userId, "active").size();
        return activeBookings < 3; // Максимум 3 активных бронирования
    }

    /**
     * Проверка доступности слота
     */
    public boolean isSlotAvailable(String machineId, String slotId) {
        Timeslot slot = timeslotRepository.findById(slotId).orElse(null);
        if (slot == null) {
            return false;
        }

        // Проверить, что слот принадлежит указанной машине
        if (!slot.getMachineId().equals(machineId)) {
            return false;
        }

        // Проверить доступность слота
        if (!slot.getIsAvailable()) {
            return false;
        }

        // Проверить, что слот не занят активным бронированием
        return !bookingRepository.existsBySlotIdAndState(slotId, "active");
    }

    /**
     * Проверка возможности отмены бронирования
     */
    private boolean canCancel(String bookingId, String userId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            return false;
        }

        // Только владелец может отменить бронирование
        if (!booking.getUserId().equals(userId)) {
            return false;
        }

        // Можно отменить только активное бронирование
        return "active".equals(booking.getState());
    }

    /**
     * Проверка возможности переноса бронирования
     */
    private boolean canReschedule(String bookingId, String newSlotId, String userId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            return false;
        }

        // Только владелец может перенести бронирование
        if (!booking.getUserId().equals(userId)) {
            return false;
        }

        // Можно перенести только активное бронирование
        if (!"active".equals(booking.getState())) {
            return false;
        }

        // Проверить доступность нового слота
        Timeslot newSlot = timeslotRepository.findById(newSlotId).orElse(null);
        if (newSlot == null || !newSlot.getIsAvailable()) {
            return false;
        }

        // Проверить, что новый слот не занят
        return !bookingRepository.existsBySlotIdAndState(newSlotId, "active");
    }
}
