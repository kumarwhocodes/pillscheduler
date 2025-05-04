package com.zerobee.pillscheduler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO representing a summary of all reminders for a specific day
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyReminderSummaryDTO {
    
    private LocalDate date;
    
    // Total reminders active for this day
    private int totalReminders;
    
    // Total doses scheduled for this day
    private int totalDoses;
    
    // Number of doses taken
    private int dosesTaken;
    
    // Number of doses missed
    private int dosesMissed;
    
    // Adherence percentage (dosesTaken/totalDoses * 100)
    private double adherencePercentage;
    
    // List of reminders with their status for this specific day
    private List<DailyReminderStatusDTO> reminderStatuses;
    
    /**
     * Nested DTO to show status of each reminder on a specific day
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyReminderStatusDTO {
        private Integer reminderId;
        private String reminderName;
        private int totalDoses;
        private int dosesTaken;
        private List<DoseDailyStatusDTO> doseStatuses;
    }
    
    /**
     * Nested DTO to show status of each dose on a specific day
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DoseDailyStatusDTO {
        private Integer doseId;
        private String doseTime;
        private boolean taken;
    }
}