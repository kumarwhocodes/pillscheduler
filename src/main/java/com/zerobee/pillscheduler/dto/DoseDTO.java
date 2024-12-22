package com.zerobee.pillscheduler.dto;

import com.zerobee.pillscheduler.entity.Dose;
import com.zerobee.pillscheduler.entity.Reminder;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DoseDTO {
    private Integer id;
    private LocalTime doseTime;
    
    public Dose toDose(Reminder reminder) {
        return Dose.builder()
                .id(id)
                .doseTime(doseTime)
                .reminder(reminder)
                .build();
    }
}
