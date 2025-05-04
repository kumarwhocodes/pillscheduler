package com.zerobee.pillscheduler.dto;

import com.zerobee.pillscheduler.entity.Dose;
import com.zerobee.pillscheduler.entity.Reminder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoseDTO {
    private Integer id;
    private LocalTime doseTime;
    
    // Indicates if the dose is taken for the current context date
    private Boolean taken;
    
    // Complete history of dates when this dose was taken
    private List<LocalDate> takenDates;
    
    public Dose toDose(Reminder reminder) {
        return Dose.builder()
                .id(id)
                .doseTime(doseTime)
                .taken(taken)
                .reminder(reminder)
                .build();
    }
}
