package com.zerobee.pillscheduler.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum ReminderType {
    MEDICINE, HEART_RATE, INSULIN, BP;
}

