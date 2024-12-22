package com.zerobee.pillscheduler.controller;

import com.zerobee.pillscheduler.dto.CustomResponse;
import com.zerobee.pillscheduler.dto.ReminderDTO;
import com.zerobee.pillscheduler.enums.Flag;
import com.zerobee.pillscheduler.enums.Status;
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
    
}
