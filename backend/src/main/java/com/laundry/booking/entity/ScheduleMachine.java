package com.laundry.booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "schedule_machines")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleMachine {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String scheduleId;

    @Column(nullable = false)
    private String machineId;
}
