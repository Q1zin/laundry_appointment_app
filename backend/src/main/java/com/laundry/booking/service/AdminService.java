package com.laundry.booking.service;

import com.laundry.booking.dto.BookingResult;
import com.laundry.booking.entity.Booking;
import com.laundry.booking.entity.Machine;
import com.laundry.booking.entity.Schedule;
import com.laundry.booking.entity.Timeslot;
import com.laundry.booking.entity.User;
import com.laundry.booking.repository.BookingRepository;
import com.laundry.booking.repository.MachineRepository;
import com.laundry.booking.repository.ScheduleRepository;
import com.laundry.booking.repository.TimeslotRepository;
import com.laundry.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MachineRepository machineRepository;
    private final ScheduleRepository scheduleRepository;
    private final BookingRepository bookingRepository;
    private final TimeslotRepository timeslotRepository;
    private final UserRepository userRepository;

    /**
     * Admin Controller - blockMachine method
     * Последовательность вызовов:
     * 1. Найти машину
     * 2. Проверить, не заблокирована ли уже
     * 3. Установить статус "blocked"
     * 4. Вернуть результат
     */
    @Transactional
    public BookingResult blockMachine(String machineId) {
        // Шаг 1: Найти машину
        Machine machine = machineRepository.findById(machineId).orElse(null);
        if (machine == null) {
            return new BookingResult(false, "Machine not found");
        }

        // Шаг 2: Проверить, не заблокирована ли уже
        if (machine.isAlreadyBlocked()) {
            return new BookingResult(false, "Machine is already blocked");
        }

        // Шаг 3: Установить статус "blocked"
        machine.setStatus("blocked");
        machineRepository.save(machine);

        return new BookingResult(true, "Machine blocked successfully");
    }

    /**
     * Admin Controller - unblockMachine method
     * Последовательность вызовов:
     * 1. Найти машину
     * 2. Установить статус "available"
     * 3. Вернуть результат
     */
    @Transactional
    public BookingResult unblockMachine(String machineId) {
        // Шаг 1: Найти машину
        Machine machine = machineRepository.findById(machineId).orElse(null);
        if (machine == null) {
            return new BookingResult(false, "Machine not found");
        }

        // Шаг 2: Установить статус "available"
        machine.setStatus("available");
        machineRepository.save(machine);

        return new BookingResult(true, "Machine unblocked successfully");
    }

    /**
     * Admin Controller - openBooking method
     * Последовательность вызовов:
     * 1. Найти или создать расписание
     * 2. Открыть для бронирования
     * 3. Пометить слоты как доступные
     * 4. Вернуть результат
     */
    @Transactional
    public BookingResult openBooking(LocalDate date) {
        // Шаг 1: Найти или создать расписание
        Schedule schedule = scheduleRepository.findByDate(date)
                .orElseGet(() -> {
                    Schedule newSchedule = new Schedule();
                    newSchedule.setDate(date);
                    newSchedule.setIsOpen(false);
                    return scheduleRepository.save(newSchedule);
                });

        // Шаг 2: Открыть для бронирования
        schedule.setIsOpen(true);
        scheduleRepository.save(schedule);

        // Шаг 3: Пометить слоты как доступные
        List<Timeslot> slots = timeslotRepository.findByDate(date);
        for (Timeslot slot : slots) {
            slot.markAvailable();
            timeslotRepository.save(slot);
        }

        return new BookingResult(true, "Booking opened successfully");
    }

    /**
     * Admin Controller - closeBooking method
     * Последовательность вызовов:
     * 1. Найти расписание
     * 2. Проверить, открыто ли
     * 3. Закрыть для бронирования
     * 4. Пометить слоты как недоступные
     * 5. Вернуть результат
     */
    @Transactional
    public BookingResult closeBooking(LocalDate date) {
        // Шаг 1: Найти расписание
        Schedule schedule = scheduleRepository.findByDate(date).orElse(null);
        if (schedule == null) {
            return new BookingResult(false, "Schedule not found");
        }

        // Шаг 2: Проверить, открыто ли
        if (!schedule.isOpen()) {
            return new BookingResult(false, "Schedule is already closed");
        }

        // Шаг 3: Закрыть для бронирования
        schedule.setIsOpen(false);
        scheduleRepository.save(schedule);

        // Шаг 4: Пометить слоты как недоступные
        List<Timeslot> slots = timeslotRepository.findByDate(date);
        for (Timeslot slot : slots) {
            slot.markUnavailable();
            timeslotRepository.save(slot);
        }

        return new BookingResult(true, "Booking closed successfully");
    }

    /**
     * Admin Controller - deleteBooking method
     * Последовательность вызовов:
     * 1. Найти бронирование
     * 2. Установить состояние "deleted"
     * 3. Освободить слот
     * 4. Вернуть результат
     */
    @Transactional
    public BookingResult deleteBooking(String bookingId) {
        // Шаг 1: Найти бронирование
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            return new BookingResult(false, "Booking not found");
        }

        // Шаг 2: Установить состояние "deleted"
        booking.setState("deleted");
        bookingRepository.save(booking);

        // Шаг 3: Освободить слот
        Timeslot slot = timeslotRepository.findById(booking.getSlotId()).orElse(null);
        if (slot != null) {
            slot.markAvailable();
            timeslotRepository.save(slot);
        }

        return new BookingResult(true, "Booking deleted successfully");
    }

    /**
     * Admin Controller - getAllUsers method
     * Возвращает список всех пользователей
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Admin Controller - blockUser method
     * Блокирует пользователя
     */
    @Transactional
    public BookingResult blockUser(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new BookingResult(false, "User not found");
        }

        if (user.getIsBlocked()) {
            return new BookingResult(false, "User is already blocked");
        }

        // Нельзя блокировать админа
        if ("admin".equals(user.getRole())) {
            return new BookingResult(false, "Cannot block admin user");
        }

        user.setIsBlocked(true);
        userRepository.save(user);

        return new BookingResult(true, "User blocked successfully");
    }

    /**
     * Admin Controller - unblockUser method
     * Разблокирует пользователя
     */
    @Transactional
    public BookingResult unblockUser(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new BookingResult(false, "User not found");
        }

        if (!user.getIsBlocked()) {
            return new BookingResult(false, "User is not blocked");
        }

        user.setIsBlocked(false);
        userRepository.save(user);

        return new BookingResult(true, "User unblocked successfully");
    }
}
