package com.laundry.booking.service;

import com.laundry.booking.dto.ScheduleData;
import com.laundry.booking.entity.Booking;
import com.laundry.booking.entity.Machine;
import com.laundry.booking.entity.Timeslot;
import com.laundry.booking.repository.BookingRepository;
import com.laundry.booking.repository.MachineRepository;
import com.laundry.booking.repository.TimeslotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final MachineRepository machineRepository;
    private final TimeslotRepository timeslotRepository;
    private final BookingRepository bookingRepository;

    /**
     * Schedule Controller - getSchedule method
     * Последовательность вызовов:
     * 1. Получить все машины
     * 2. Получить слоты на дату
     * 3. Получить бронирования на дату
     * 4. Собрать данные расписания
     * 5. Вернуть расписание
     */
    public ScheduleData getSchedule(LocalDate date, String userId) {
        // Шаг 1: Получить все машины
        List<Machine> machines = machineRepository.findAll();

        // Шаг 2: Получить слоты на дату
        List<Timeslot> slots = timeslotRepository.findByDate(date);

        // Шаг 3: Получить бронирования на дату
        List<Booking> bookings = bookingRepository.findByDate(date);

        // Шаг 4: Собрать данные расписания
        ScheduleData schedule = new ScheduleData();
        schedule.setMachines(machines);
        schedule.setTimeslots(slots);
        schedule.setBookings(bookings);

        return schedule;
    }
}
