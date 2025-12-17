package com.laundry.booking.repository;

import com.laundry.booking.entity.Timeslot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeslotRepository extends JpaRepository<Timeslot, String> {
    
    @Query("SELECT t FROM Timeslot t WHERE DATE(t.startTime) = :date")
    List<Timeslot> findByDate(@Param("date") LocalDate date);
    
    Optional<Timeslot> findByMachineIdAndId(String machineId, String id);
    
    List<Timeslot> findByMachineId(String machineId);
}
