package com.zerobee.pillscheduler.entity;

import com.zerobee.pillscheduler.dto.DoseDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "doses")
public class Dose {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private LocalTime doseTime;
    
    @Column(nullable = false)
    private Boolean taken = false;
    
    @ManyToOne
    @JoinColumn(name = "reminder_id", nullable = false)
    private Reminder reminder;
    
    public DoseDTO toDoseDTO() {
        return DoseDTO.builder()
                .id(id)
                .doseTime(doseTime)
                .taken(taken)
                .build();
    }
}