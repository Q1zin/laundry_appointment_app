package com.laundry.booking.service;

import com.laundry.booking.dto.BookingResult;
import com.laundry.booking.dto.UserBookingDto;
import com.laundry.booking.entity.Booking;
import com.laundry.booking.entity.Machine;
import com.laundry.booking.entity.Timeslot;
import com.laundry.booking.entity.User;
import com.laundry.booking.repository.BookingRepository;
import com.laundry.booking.repository.MachineRepository;
import com.laundry.booking.repository.TimeslotRepository;
import com.laundry.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TimeslotRepository timeslotRepository;
    private final UserRepository userRepository;
    private final MachineRepository machineRepository;

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
            return new BookingResult(false, "Вы достигли лимита активных записей (максимум 2)");
        }

        // Шаг 1.2: Проверка доступности слота
        if (!isSlotAvailable(machineId, slotId)) {
            return new BookingResult(false, "Слот недоступен");
        }

        // Шаг 1.3: Повторная проверка лимита внутри транзакции (защита от race condition)
        // Считаем только будущие записи
        long futureBookings = countFutureActiveBookings(userId);
        if (futureBookings >= 2) {
            return new BookingResult(false, "Вы достигли лимита активных записей (максимум 2)");
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

        return new BookingResult(true, "Запись успешно создана");
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
            return new BookingResult(false, "Невозможно отменить эту запись");
        }

        // Шаг 2: Найти и отменить бронирование
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            return new BookingResult(false, "Запись не найдена");
        }

        booking.setState("canceled");
        bookingRepository.save(booking);

        // Шаг 3: Освободить слот
        Timeslot slot = timeslotRepository.findById(booking.getSlotId()).orElse(null);
        if (slot != null) {
            slot.free();
            timeslotRepository.save(slot);
        }

        return new BookingResult(true, "Запись успешно отменена");
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
            return new BookingResult(false, "Невозможно перенести эту запись");
        }

        // Шаг 2: Загрузить бронирование
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            return new BookingResult(false, "Запись не найдена");
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

        return new BookingResult(true, "Запись успешно перенесена");
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

        // Проверить количество будущих активных бронирований (прошедшие не считаются)
        long futureBookings = countFutureActiveBookings(userId);
        return futureBookings < 2; // Максимум 2 будущих бронирования
    }

    /**
     * Подсчёт будущих активных бронирований пользователя
     */
    private long countFutureActiveBookings(String userId) {
        List<Booking> activeBookings = bookingRepository.findByUserIdAndState(userId, "active");
        LocalDateTime now = LocalDateTime.now();
        
        return activeBookings.stream()
            .filter(booking -> {
                Timeslot slot = timeslotRepository.findById(booking.getSlotId()).orElse(null);
                if (slot == null) return false;
                // Слот считается будущим, если его конец ещё не наступил
                return slot.getEndTime() != null && slot.getEndTime().isAfter(now);
            })
            .count();
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

    /**
     * Получить все активные записи пользователя с информацией о машинках и слотах
     * Возвращает все записи (и будущие, и прошедшие)
     */
    public List<UserBookingDto> getUserBookings(String userId) {
        List<Booking> bookings = bookingRepository.findByUserIdAndState(userId, "active");
        List<UserBookingDto> result = new ArrayList<>();

        for (Booking booking : bookings) {
            UserBookingDto dto = new UserBookingDto();
            dto.setId(booking.getId());
            dto.setMachineId(booking.getMachineId());
            dto.setSlotId(booking.getSlotId());
            dto.setState(booking.getState());
            dto.setCreatedAt(booking.getCreatedAt());

            // Получаем данные машинки
            Machine machine = machineRepository.findById(booking.getMachineId()).orElse(null);
            if (machine != null) {
                dto.setMachineName(machine.getName());
            }

            // Получаем данные слота
            Timeslot slot = timeslotRepository.findById(booking.getSlotId()).orElse(null);
            if (slot != null) {
                dto.setSlotStartTime(slot.getStartTime());
                dto.setSlotEndTime(slot.getEndTime());
                // Помечаем как прошедшую, если время окончания уже прошло
                if (slot.getEndTime() != null && slot.getEndTime().isBefore(LocalDateTime.now())) {
                    dto.setState("past");
                }
            }

            result.add(dto);
        }

        return result;
    }
}
