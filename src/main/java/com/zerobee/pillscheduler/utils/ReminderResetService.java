package com.zerobee.pillscheduler.utils;

import com.zerobee.pillscheduler.entity.Reminder;
import com.zerobee.pillscheduler.enums.Status;
import com.zerobee.pillscheduler.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class ReminderResetService {
    
    private final ReminderRepository reminderRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional // Ensures a session is available
    public void resetRemainingDoses() {
        log.info("Starting daily reset of remaining doses...");
        List<Reminder> reminders = reminderRepository.findAll(); // Fetch reminders
        reminders.forEach(reminder -> {
            int doseCount = reminder.getDoses().size(); // Access lazy-loaded doses
            reminder.setRemaining_doses(doseCount);
            reminder.setStatus(Status.NOT_TAKEN);
        });
        reminderRepository.saveAll(reminders); // Save back to the database
        log.info("Remaining doses reset for all reminders.");
    }
}
