package com.zerobee.pillscheduler.repository;

import com.zerobee.pillscheduler.entity.Reminder;
import com.zerobee.pillscheduler.enums.Flag;
import com.zerobee.pillscheduler.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Integer> {
    List<Reminder> findByUserId(String userId);
    List<Reminder> findByUserIdAndFlagAndStatus(String userId, Flag flag, Status status);
}

