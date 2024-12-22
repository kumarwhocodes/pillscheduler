package com.zerobee.pillscheduler.dto;

import com.zerobee.pillscheduler.entity.Reminder;
import com.zerobee.pillscheduler.enums.Flag;
import com.zerobee.pillscheduler.enums.ReminderType;
import com.zerobee.pillscheduler.enums.Status;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReminderDTO {
    private Integer id;
    private String r_name;
    private String r_photo;
    private ReminderType r_type; // medicine, heart rate, insulin, BP
    private String category; // tablet, syrup (applicable for medicines)
    private List<DoseDTO> doses;
    private Integer remaining_doses;
    private String frequency;
    private LocalDateTime start_date_time;
    private LocalDateTime end_date_time;
    private String notes;
    private Flag flag;
    private Status status;
    private UserDTO user;
    
    public Reminder toReminder() {
        return Reminder.builder()
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
                .user(user != null ? user.toUser() : null)
                .doses(doses != null ? doses.stream()
                        .map(d -> d.toDose(null))
                        .collect(Collectors.toList())
                        : null)
                .remaining_doses(remaining_doses)
                .build();
    }
}
