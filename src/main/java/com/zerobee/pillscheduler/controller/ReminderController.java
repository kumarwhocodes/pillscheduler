package com.zerobee.pillscheduler.controller;

import com.zerobee.pillscheduler.dto.CustomResponse;
import com.zerobee.pillscheduler.dto.ReminderDTO;
import com.zerobee.pillscheduler.enums.Frequency;
import com.zerobee.pillscheduler.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    public CustomResponse<List<ReminderDTO>> fetchReminders(
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
    
    @GetMapping("/fetch/active")
    public CustomResponse<List<ReminderDTO>> fetchActiveReminders(
            @RequestHeader("Authorization") String token,
            @RequestParam("flag") String flag,
            @RequestParam("status") String status
    ) {
        List<ReminderDTO> activeReminders = reminderService.fetchActiveReminders(token, flag, status);
        return new CustomResponse<>(
                HttpStatus.OK,
                "Active Reminders Fetched Successfully!",
                activeReminders
        );
    }
    
    @GetMapping("/fetch/by-frequency")
    public CustomResponse<List<ReminderDTO>> fetchRemindersByFrequency(
            @RequestHeader("Authorization") String token,
            @RequestParam("frequency") Frequency frequency
    ) {
        List<ReminderDTO> frequencyReminders = reminderService.fetchRemindersByFrequency(token, frequency);
        return new CustomResponse<>(
                HttpStatus.OK,
                "Reminders fetched by frequency successfully!",
                frequencyReminders
        );
    }
    
    @GetMapping("/fetch/active-by-frequency")
    public CustomResponse<List<ReminderDTO>> fetchActiveRemindersByFrequency(
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
                "Active Reminders by Frequency Fetched Successfully!",
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
    
    @PutMapping("/mark-taken/{reminderId}/{doseId}")
    public CustomResponse<Void> markDoseAsTaken(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer reminderId,
            @PathVariable Integer doseId
    ) {
        reminderService.markDoseAsTaken(token, reminderId, doseId);
        return new CustomResponse<>(
                HttpStatus.OK,
                "Dose marked as taken successfully.",
                null
        );
    }
    
    
}
