package com.zerobee.pillscheduler.controller;

import com.zerobee.pillscheduler.dto.CustomResponse;
import com.zerobee.pillscheduler.dto.ReminderDTO;
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
    public CustomResponse<List<ReminderDTO>> getReminders(
            @RequestHeader("Authorization") String token) {
        
        List<ReminderDTO> reminders = reminderService.getRemindersForUser(token);
        return new CustomResponse<>(
                HttpStatus.OK,
                "Reminders fetched successfully",
                reminders
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
