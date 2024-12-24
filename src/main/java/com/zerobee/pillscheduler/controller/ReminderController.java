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
}
