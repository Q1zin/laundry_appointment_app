package com.laundry.booking.repository;

import com.laundry.booking.entity.ScheduleMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleMachineRepository extends JpaRepository<ScheduleMachine, String> {
    
    List<ScheduleMachine> findByScheduleId(String scheduleId);
    
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ScheduleMachine sm WHERE sm.scheduleId = :scheduleId")
    void deleteByScheduleId(@Param("scheduleId") String scheduleId);
    
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ScheduleMachine sm WHERE sm.machineId = :machineId")
    void deleteByMachineId(@Param("machineId") String machineId);
}
