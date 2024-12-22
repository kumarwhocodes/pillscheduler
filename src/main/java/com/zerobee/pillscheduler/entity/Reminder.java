package com.zerobee.pillscheduler.entity;

import com.zerobee.pillscheduler.dto.ReminderDTO;
import com.zerobee.pillscheduler.enums.Flag;
import com.zerobee.pillscheduler.enums.ReminderType;
import com.zerobee.pillscheduler.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reminders")
public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String r_name;
    private String r_photo;
    
    @Enumerated(EnumType.STRING)
    private ReminderType r_type; // medicine, heart rate, insulin, BP
    
    private String category; // tablet, syrup (applicable for medicines)
    private String frequency;
    private LocalDateTime start_date_time;
    private LocalDateTime end_date_time;
    private String notes;
    
    @Enumerated(EnumType.STRING)
    private Flag flag;
    
    @Enumerated(EnumType.STRING)
    private Status status;
    
    @OneToMany(mappedBy = "reminder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Dose> doses = new ArrayList<>();
    
    private Integer remaining_doses;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    public ReminderDTO toReminderDTO() {
        return ReminderDTO.builder()
                .id(id)
                .r_name(r_name)
                .r_photo(r_photo)
                .r_type(r_type)
                .category(category)
                .frequency(frequency)
                .start_date_time(start_date_time)
                .end_date_time(end_date_time)
                .notes(notes)
                .flag(flag)
                .status(status)
                .doses(doses.stream().map(Dose::toDoseDTO).toList())
                .remaining_doses(remaining_doses)
                .build();
    }
}
