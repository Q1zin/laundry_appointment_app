package com.laundry.booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String machineId;

    @Column(nullable = false)
    private String slotId;

    @Column(nullable = false)
    private String state = "active"; // active, canceled, deleted

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Business methods
    public void setState(String state) {
        this.state = state;
    }

    public String getSlotId() {
        return this.slotId;
    }

    public void setSlot(String slotId) {
        this.slotId = slotId;
    }
}
