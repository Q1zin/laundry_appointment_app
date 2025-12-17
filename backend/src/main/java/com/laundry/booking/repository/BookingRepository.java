package com.laundry.booking.repository;

import com.laundry.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {
    
    @Query("SELECT b FROM Booking b JOIN Timeslot t ON b.slotId = t.id WHERE DATE(t.startTime) = :date")
    List<Booking> findByDate(@Param("date") LocalDate date);
    
    List<Booking> findByUserId(String userId);
    
    List<Booking> findByUserIdAndState(String userId, String state);
    
    boolean existsBySlotIdAndState(String slotId, String state);
}
