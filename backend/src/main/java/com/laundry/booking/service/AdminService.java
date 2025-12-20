package com.laundry.booking.service;

import com.laundry.booking.dto.AdminBookingDto;
import com.laundry.booking.dto.BookingResult;
import com.laundry.booking.dto.ScheduleDto;
import com.laundry.booking.dto.ScheduleRequest;
import com.laundry.booking.entity.Booking;
import com.laundry.booking.entity.Machine;
import com.laundry.booking.entity.Schedule;
import com.laundry.booking.entity.ScheduleMachine;
import com.laundry.booking.entity.Timeslot;
import com.laundry.booking.entity.User;
import com.laundry.booking.repository.BookingRepository;
import com.laundry.booking.repository.MachineRepository;
import com.laundry.booking.repository.ScheduleMachineRepository;
import com.laundry.booking.repository.ScheduleRepository;
import com.laundry.booking.repository.TimeslotRepository;
import com.laundry.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MachineRepository machineRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleMachineRepository scheduleMachineRepository;
    private final BookingRepository bookingRepository;
    private final TimeslotRepository timeslotRepository;
    private final UserRepository userRepository;

    // ============= MACHINES =============

    /**
     * Получить все машинки
     */
    public List<Machine> getAllMachines() {
        return machineRepository.findAll();
    }

    /**
     * Создать новую машинку
     */
    @Transactional
    public Machine createMachine(String name) {
        Machine machine = new Machine();
        machine.setName(name);
        machine.setStatus("available");
        return machineRepository.save(machine);
    }

    /**
     * Удалить машинку
     */
    @Transactional
    public BookingResult deleteMachine(String machineId) {
        Machine machine = machineRepository.findById(machineId).orElse(null);
        if (machine == null) {
            return new BookingResult(false, "Машинка не найдена");
        }

        // Удаляем связи с расписаниями
        scheduleMachineRepository.deleteByMachineId(machineId);
        
        // Удаляем машинку (каскадно удалятся timeslots и bookings)
        machineRepository.delete(machine);

        return new BookingResult(true, "Машинка успешно удалена");
    }
    @Transactional
    public BookingResult blockMachine(String machineId) {
        // Шаг 1: Найти машину
        Machine machine = machineRepository.findById(machineId).orElse(null);
        if (machine == null) {
            return new BookingResult(false, "Машинка не найдена");
        }

        // Шаг 2: Проверить, не заблокирована ли уже
        if (machine.isAlreadyBlocked()) {
            return new BookingResult(false, "Машинка уже заблокирована");
        }

        // Шаг 3: Установить статус "blocked"
        machine.setStatus("blocked");
        machineRepository.save(machine);

        return new BookingResult(true, "Машинка успешно заблокирована");
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
            return new BookingResult(false, "Машинка не найдена");
        }

        // Шаг 2: Установить статус "available"
        machine.setStatus("available");
        machineRepository.save(machine);

        return new BookingResult(true, "Машинка успешно разблокирована");
    }

    // ============= SCHEDULES =============

    /**
     * Получить все расписания
     */
    public List<ScheduleDto> getAllSchedules() {
        List<Schedule> schedules = scheduleRepository.findAll();
        List<ScheduleDto> result = new ArrayList<>();

        for (Schedule schedule : schedules) {
            ScheduleDto dto = new ScheduleDto();
            dto.setId(schedule.getId());
            dto.setDate(schedule.getDate());
            dto.setIsOpen(schedule.getIsOpen());
            dto.setCreatedAt(schedule.getCreatedAt());

            // Получаем машинки для этого расписания
            List<ScheduleMachine> scheduleMachines = scheduleMachineRepository.findByScheduleId(schedule.getId());
            List<String> machineIds = scheduleMachines.stream()
                    .map(ScheduleMachine::getMachineId)
                    .collect(Collectors.toList());
            dto.setMachineIds(machineIds);

            result.add(dto);
        }

        return result;
    }

    /**
     * Создать или обновить расписание
     */
    @Transactional
    public ScheduleDto createOrUpdateSchedule(ScheduleRequest request) {
        // Найти или создать расписание
        Schedule schedule = scheduleRepository.findByDate(request.getDate())
                .orElseGet(() -> {
                    Schedule newSchedule = new Schedule();
                    newSchedule.setDate(request.getDate());
                    return newSchedule;
                });

        // Сначала сохраняем schedule, чтобы получить id (для нового)
        schedule = scheduleRepository.saveAndFlush(schedule);
        
        // Удаляем старые связи с машинками (clearAutomatically очистит контекст)
        scheduleMachineRepository.deleteByScheduleId(schedule.getId());
        
        // ПОСЛЕ очистки контекста — заново загружаем schedule и обновляем isOpen
        schedule = scheduleRepository.findById(schedule.getId()).orElseThrow();
        schedule.setIsOpen(request.getIsOpen());
        schedule = scheduleRepository.saveAndFlush(schedule);

        // Создаём новые связи
        if (request.getMachineIds() != null) {
            for (String machineId : request.getMachineIds()) {
                ScheduleMachine sm = new ScheduleMachine();
                sm.setScheduleId(schedule.getId());
                sm.setMachineId(machineId);
                scheduleMachineRepository.save(sm);
            }
        }

        // Создаём или обновляем временные слоты для выбранных машинок и времени
        if (Boolean.TRUE.equals(request.getIsOpen()) && request.getMachineIds() != null && !request.getMachineIds().isEmpty()) {
            // Если временные слоты указаны, используем их, иначе - все по умолчанию
            if (request.getTimeSlots() != null && !request.getTimeSlots().isEmpty()) {
                createTimeslotsForDate(request.getDate(), request.getMachineIds(), request.getTimeSlots());
            } else {
                createTimeslotsForDate(request.getDate(), request.getMachineIds(), null);
            }
        }

        // Возвращаем DTO
        ScheduleDto dto = new ScheduleDto();
        dto.setId(schedule.getId());
        dto.setDate(schedule.getDate());
        dto.setIsOpen(schedule.getIsOpen());
        dto.setMachineIds(request.getMachineIds());
        dto.setCreatedAt(schedule.getCreatedAt());

        return dto;
    }

    /**
     * Создать временные слоты для даты и машинок
     * @param date дата
     * @param machineIds список ID машинок
     * @param timeSlots список временных слотов в формате "HH:mm-HH:mm" или null для всех слотов по умолчанию
     */
    private void createTimeslotsForDate(LocalDate date, List<String> machineIds, List<String> timeSlots) {
        // Сначала удаляем существующие слоты для этой даты и этих машинок
        for (String machineId : machineIds) {
            List<Timeslot> existingSlots = timeslotRepository.findByMachineIdAndDate(machineId, date);
            timeslotRepository.deleteAll(existingSlots);
        }

        // Если временные слоты не указаны, создаем все по умолчанию
        if (timeSlots == null || timeSlots.isEmpty()) {
            int[][] defaultSlots = {{8, 10}, {10, 12}, {12, 14}, {14, 16}, {16, 18}, {18, 20}, {20, 22}};
            for (String machineId : machineIds) {
                for (int[] slot : defaultSlots) {
                    LocalDateTime startTime = LocalDateTime.of(date, LocalTime.of(slot[0], 0));
                    LocalDateTime endTime = LocalDateTime.of(date, LocalTime.of(slot[1], 0));
                    
                    Timeslot timeslot = new Timeslot();
                    timeslot.setMachineId(machineId);
                    timeslot.setStartTime(startTime);
                    timeslot.setEndTime(endTime);
                    timeslot.setIsAvailable(true);
                    timeslotRepository.save(timeslot);
                }
            }
        } else {
            // Создаем только выбранные слоты
            for (String machineId : machineIds) {
                for (String timeSlot : timeSlots) {
                    // Парсим формат "08:00-10:00"
                    String[] parts = timeSlot.split("-");
                    if (parts.length == 2) {
                        String[] startParts = parts[0].split(":");
                        String[] endParts = parts[1].split(":");
                        
                        int startHour = Integer.parseInt(startParts[0]);
                        int startMinute = Integer.parseInt(startParts[1]);
                        int endHour = Integer.parseInt(endParts[0]);
                        int endMinute = Integer.parseInt(endParts[1]);
                        
                        LocalDateTime startTime = LocalDateTime.of(date, LocalTime.of(startHour, startMinute));
                        LocalDateTime endTime = LocalDateTime.of(date, LocalTime.of(endHour, endMinute));
                        
                        Timeslot timeslot = new Timeslot();
                        timeslot.setMachineId(machineId);
                        timeslot.setStartTime(startTime);
                        timeslot.setEndTime(endTime);
                        timeslot.setIsAvailable(true);
                        timeslotRepository.save(timeslot);
                    }
                }
            }
        }
    }

    /**
     * Удалить расписание
     */
    @Transactional
    public BookingResult deleteSchedule(String scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElse(null);
        if (schedule == null) {
            return new BookingResult(false, "Расписание не найдено");
        }

        // Удаляем связи с машинками
        scheduleMachineRepository.deleteByScheduleId(scheduleId);

        // Удаляем расписание
        scheduleRepository.delete(schedule);

        return new BookingResult(true, "Расписание успешно удалено");
    }

    // ============= BOOKINGS =============

    /**
     * Удалить бронирование
     */
    @Transactional
    public BookingResult deleteBooking(String bookingId) {
        // Шаг 1: Найти бронирование
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            return new BookingResult(false, "Запись не найдена");
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

        return new BookingResult(true, "Запись успешно удалена");
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
            return new BookingResult(false, "Пользователь не найден");
        }

        if (user.getIsBlocked()) {
            return new BookingResult(false, "Пользователь уже заблокирован");
        }

        // Нельзя блокировать админа
        if ("admin".equals(user.getRole())) {
            return new BookingResult(false, "Невозможно заблокировать администратора");
        }

        user.setIsBlocked(true);
        userRepository.save(user);

        return new BookingResult(true, "Пользователь успешно заблокирован");
    }

    /**
     * Admin Controller - unblockUser method
     * Разблокирует пользователя
     */
    @Transactional
    public BookingResult unblockUser(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new BookingResult(false, "Пользователь не найден");
        }

        if (!user.getIsBlocked()) {
            return new BookingResult(false, "Пользователь не заблокирован");
        }

        user.setIsBlocked(false);
        userRepository.save(user);

        return new BookingResult(true, "Пользователь успешно разблокирован");
    }

    /**
     * Admin Controller - getAllBookingsWithDetails method
     * Возвращает все бронирования с деталями
     */
    public List<AdminBookingDto> getAllBookingsWithDetails() {
        List<Booking> bookings = bookingRepository.findAll();
        List<AdminBookingDto> result = new ArrayList<>();

        for (Booking booking : bookings) {
            AdminBookingDto dto = new AdminBookingDto();
            dto.setId(booking.getId());
            dto.setUserId(booking.getUserId());
            dto.setMachineId(booking.getMachineId());
            dto.setSlotId(booking.getSlotId());
            dto.setState(booking.getState());
            dto.setCreatedAt(booking.getCreatedAt());

            // Получаем данные пользователя
            User user = userRepository.findById(booking.getUserId()).orElse(null);
            if (user != null) {
                dto.setUserName(user.getName());
                dto.setUserFullName(user.getFullName());
                dto.setUserRoom(user.getRoom());
            }

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
            }

            result.add(dto);
        }

        return result;
    }
}
