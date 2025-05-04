package com.zerobee.pillscheduler.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dose_taken_records", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"dose_id", "date"})
})
public class DoseTakenRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "dose_id", nullable = false)
    private Dose dose;
    
    @Column(nullable = false)
    private LocalDate date;
    
}