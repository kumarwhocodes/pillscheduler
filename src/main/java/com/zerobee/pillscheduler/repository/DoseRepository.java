package com.zerobee.pillscheduler.repository;

import com.zerobee.pillscheduler.entity.Dose;
import com.zerobee.pillscheduler.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DoseRepository extends JpaRepository<Dose, Integer> {
    List<Dose> findByReminder(Reminder reminder);
    
//    List<Dose> findByReminderAndDoseDate(Reminder reminder, LocalDate date);
//
//    List<Dose> findByDoseDate(LocalDate date);
}