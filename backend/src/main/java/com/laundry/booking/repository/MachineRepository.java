package com.laundry.booking.repository;

import com.laundry.booking.entity.Machine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MachineRepository extends JpaRepository<Machine, String> {
    
    List<Machine> findByStatus(String status);
}
