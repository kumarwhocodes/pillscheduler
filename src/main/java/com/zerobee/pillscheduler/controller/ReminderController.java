package com.zerobee.pillscheduler.controller;

import com.zerobee.pillscheduler.dto.CustomResponse;
import com.zerobee.pillscheduler.dto.DailyReminderSummaryDTO;
import com.zerobee.pillscheduler.dto.ReminderDTO;
import com.zerobee.pillscheduler.enums.Frequency;
import com.zerobee.pillscheduler.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reminder")
@RequiredArgsConstructor
public class ReminderController {
    
    private final ReminderService reminderService;
    
    @PostMapping("/create")
    public CustomResponse<ReminderDTO> createReminder(
            @RequestHeader("Authorization") String token,
            @RequestBody ReminderDTO requestDTO) {
        
        ReminderDTO reminderResponse = reminderService.createReminder(token, requestDTO);
        return new CustomResponse<>(
                HttpStatus.CREATED,
                "Reminder created successfully",
                reminderResponse
        );
    }
    
    @GetMapping("/fetch")
    public CustomResponse<List<ReminderDTO>> fetchAllReminders(
            @RequestHeader("Authorization") String token) {
        
        List<ReminderDTO> reminders = reminderService.fetchRemindersForUser(token);
        return new CustomResponse<>(
                HttpStatus.OK,
                "Reminders fetched successfully",
                reminders
        );
    }
    
    @GetMapping("/fetch/{id}")
    public CustomResponse<ReminderDTO> fetchReminderById(
            @RequestHeader("Authorization") String token,
            @PathVariable("id") Integer id
    ) {
        ReminderDTO reminderDTO = reminderService.fetchReminderById(token, id);
        return new CustomResponse<>(
                HttpStatus.OK,
                "Reminder fetched successfully",
                reminderDTO
        );
    }
    
    @GetMapping("/fetch/filters")
    public CustomResponse<List<ReminderDTO>> fetchFilteredReminders(
            @RequestHeader("Authorization") String token,
            @RequestParam("flag") String flag,
            @RequestParam("status") String status,
            @RequestParam("frequency") Frequency frequency
    ) {
        List<ReminderDTO> activeFrequencyReminders = reminderService.fetchActiveRemindersByFrequency(
                token, flag, status, frequency
        );
        return new CustomResponse<>(
                HttpStatus.OK,
                "Filtered Reminders Fetched Successfully!",
                activeFrequencyReminders
        );
    }
    
    @DeleteMapping("/delete/{id}")
    public CustomResponse<Void> deleteReminder(
            @RequestHeader("Authorization") String token,
            @PathVariable("id") Integer id
    ) {
        reminderService.deleteReminderById(token, id);
        return new CustomResponse<>(
                HttpStatus.OK,
                "Reminder deleted successfully.",
                null
        );
    }
    
    @PutMapping("/mark-taken/{reminderId}/{doseId}/{date}")
    public CustomResponse<Void> markDoseAsTakenForDate(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer reminderId,
            @PathVariable Integer doseId,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
    ) {
        reminderService.markDoseAsTakenForDate(token, reminderId, doseId, date);
        return new CustomResponse<>(
                HttpStatus.OK,
                "Dose marked as taken for specified date successfully.",
                null
        );
    }
    
    @PutMapping("/mark-not-taken/{reminderId}/{doseId}/{date}")
    public CustomResponse<Void> markDoseAsNotTakenForDate(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer reminderId,
            @PathVariable Integer doseId,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
    ) {
        reminderService.markDoseAsNotTakenForDate(token, reminderId, doseId, date);
        return new CustomResponse<>(
                HttpStatus.OK,
                "Dose marked as not taken for specified date successfully.",
                null
        );
    }
    
    // NEW ENDPOINTS FOR DAY-WISE TRACKING
    
    /**
     * Retrieves all reminders for a specific date with their dose status
     */
    @GetMapping("/by-date/{date}")
    public CustomResponse<List<ReminderDTO>> getRemindersForDate(
            @RequestHeader("Authorization") String token,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
    ) {
        List<ReminderDTO> reminders = reminderService.fetchRemindersForDate(token, date);
        return new CustomResponse<>(
                HttpStatus.OK,
                "Reminders for date " + date + " fetched successfully",
                reminders
        );
    }
    
    /**
     * Retrieves daily tracking summary for a date range
     */
    @GetMapping("/history")
    public CustomResponse<List<DailyReminderSummaryDTO>> getReminderHistory(
            @RequestHeader("Authorization") String token,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        List<DailyReminderSummaryDTO> history = reminderService.fetchReminderHistory(token, startDate, endDate);
        return new CustomResponse<>(
                HttpStatus.OK,
                "Reminder history fetched successfully",
                history
        );
    }
    
    /**
     * Get a specific day's summary (total reminders, taken/missed doses)
     */
    @GetMapping("/daily-summary/{date}")
    public CustomResponse<DailyReminderSummaryDTO> getDailySummary(
            @RequestHeader("Authorization") String token,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
    ) {
        DailyReminderSummaryDTO summary = reminderService.getDailySummary(token, date);
        return new CustomResponse<>(
                HttpStatus.OK,
                "Daily summary for " + date + " fetched successfully",
                summary
        );
    }
}