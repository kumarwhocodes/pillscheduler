package com.zerobee.pillscheduler.utils;

import com.zerobee.pillscheduler.entity.Reminder;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

public interface FrequencyLogic {
    default boolean isAlternateDayReminder(Reminder reminder, LocalDateTime now) {
        long daysSinceStart = ChronoUnit.DAYS.between(reminder.getStart_date_time(), now);
        return daysSinceStart % 2 == 0;
    }
    
    default boolean isCustomDayReminder(Reminder reminder, DayOfWeek currentDay) {
        if (reminder.getDays() == null) return false;
        return Arrays.stream(reminder.getDays().split(","))
                .anyMatch(day -> day.trim().equalsIgnoreCase(currentDay.name()));
    }
}
