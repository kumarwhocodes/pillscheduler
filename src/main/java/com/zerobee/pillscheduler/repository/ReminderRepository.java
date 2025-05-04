package com.zerobee.pillscheduler.repository;

import com.zerobee.pillscheduler.entity.Reminder;
import com.zerobee.pillscheduler.entity.User;
import com.zerobee.pillscheduler.enums.Flag;
import com.zerobee.pillscheduler.enums.Frequency;
import com.zerobee.pillscheduler.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Integer> {
    List<Reminder> findByUserAndFlagAndStatusAndFrequency(User user, Flag flag, Status status, Frequency frequency);
    
    List<Reminder> findByUserId(String userId);
}

