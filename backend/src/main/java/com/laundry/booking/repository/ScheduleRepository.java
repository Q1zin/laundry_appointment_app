package com.laundry.booking.repository;

import com.laundry.booking.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, String> {
    
    Optional<Schedule> findByDate(LocalDate date);
}
