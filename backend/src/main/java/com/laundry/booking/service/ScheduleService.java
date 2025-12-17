package com.laundry.booking.service;

import com.laundry.booking.dto.ScheduleData;
import com.laundry.booking.entity.Booking;
import com.laundry.booking.entity.Machine;
import com.laundry.booking.entity.Schedule;
import com.laundry.booking.entity.ScheduleMachine;
import com.laundry.booking.entity.Timeslot;
import com.laundry.booking.repository.BookingRepository;
import com.laundry.booking.repository.MachineRepository;
import com.laundry.booking.repository.ScheduleMachineRepository;
import com.laundry.booking.repository.ScheduleRepository;
import com.laundry.booking.repository.TimeslotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final MachineRepository machineRepository;
    private final TimeslotRepository timeslotRepository;
    private final BookingRepository bookingRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleMachineRepository scheduleMachineRepository;

    /**
     * Schedule Controller - getSchedule method
     * Последовательность вызовов:
     * 1. Получить все машины
     * 2. Проверить расписание на эту дату
     * 3. Если записи закрыты - вернуть пустые слоты
     * 4. Получить слоты на дату (только для разрешённых машинок)
     * 5. Получить бронирования на дату
     * 6. Собрать данные расписания
     * 7. Вернуть расписание
     */
    public ScheduleData getSchedule(LocalDate date, String userId) {
        // Шаг 1: Получить все машины
        List<Machine> allMachines = machineRepository.findAll();

        // Шаг 2: Проверить расписание на эту дату
        Optional<Schedule> scheduleOpt = scheduleRepository.findByDate(date);
        
        List<Machine> availableMachines;
        List<Timeslot> slots;
        
        if (scheduleOpt.isPresent()) {
            Schedule schedule = scheduleOpt.get();
            
            // Шаг 3: Если записи закрыты - вернуть пустые данные
            if (!schedule.getIsOpen()) {
                ScheduleData result = new ScheduleData();
                result.setMachines(new ArrayList<>()); // Пустой список машинок
                result.setTimeslots(new ArrayList<>());
                result.setBookings(new ArrayList<>());
                return result;
            }
            
            // Шаг 4: Получить разрешённые машинки для этой даты
            List<ScheduleMachine> scheduleMachines = scheduleMachineRepository.findByScheduleId(schedule.getId());
            Set<String> allowedMachineIds = scheduleMachines.stream()
                    .map(ScheduleMachine::getMachineId)
                    .collect(Collectors.toSet());
            
            // Если список машинок пустой - значит НИ ОДНА машинка не доступна для записи
            // (запись открыта, но машинки не выбраны - записаться нельзя)
            if (allowedMachineIds.isEmpty()) {
                availableMachines = new ArrayList<>();
                slots = new ArrayList<>();
            } else {
                // Фильтруем машинки - только те, что в расписании и не заблокированы
                availableMachines = allMachines.stream()
                        .filter(m -> allowedMachineIds.contains(m.getId()) && m.getStatus().equals("available"))
                        .collect(Collectors.toList());
                
                // Получить слоты только для разрешённых машинок
                slots = timeslotRepository.findByDate(date).stream()
                        .filter(slot -> allowedMachineIds.contains(slot.getMachineId()))
                        .collect(Collectors.toList());
            }
        } else {
            // Нет настроек расписания - все незаблокированные машинки доступны
            availableMachines = allMachines.stream()
                    .filter(m -> m.getStatus().equals("available"))
                    .collect(Collectors.toList());
            slots = timeslotRepository.findByDate(date);
        }

        // Шаг 5: Получить бронирования на дату
        List<Booking> bookings = bookingRepository.findByDate(date);

        // Шаг 6: Собрать данные расписания
        ScheduleData scheduleData = new ScheduleData();
        scheduleData.setMachines(availableMachines);
        scheduleData.setTimeslots(slots);
        scheduleData.setBookings(bookings);

        return scheduleData;
    }
}
