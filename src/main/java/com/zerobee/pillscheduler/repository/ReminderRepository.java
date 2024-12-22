package com.zerobee.pillscheduler.repository;

import com.zerobee.pillscheduler.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Integer> {
    
}

