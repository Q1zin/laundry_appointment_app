package com.laundry.booking.repository;

import com.laundry.booking.entity.ScheduleMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleMachineRepository extends JpaRepository<ScheduleMachine, String> {
    
    List<ScheduleMachine> findByScheduleId(String scheduleId);
    
    void deleteByScheduleId(String scheduleId);
    
    void deleteByMachineId(String machineId);
}
