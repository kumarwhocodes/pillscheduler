package com.zerobee.pillscheduler.repository;

import com.zerobee.pillscheduler.entity.Dose;
import com.zerobee.pillscheduler.entity.DoseTakenRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoseTakenRecordRepository extends JpaRepository<DoseTakenRecord, Integer> {
    boolean existsByDoseAndDate(Dose dose, LocalDate date);
    
    Optional<DoseTakenRecord> findByDoseAndDate(Dose dose, LocalDate date);
    
    List<DoseTakenRecord> findByDose(Dose dose);
    
    List<DoseTakenRecord> findByDoseInAndDate(List<Dose> doses, LocalDate date);
    
    // New methods for day-wise tracking
    
    /**
     * Find all dose taken records for a user on a specific date
     */
    @Query("SELECT dtr FROM DoseTakenRecord dtr " +
            "JOIN dtr.dose d " +
            "JOIN d.reminder r " +
            "WHERE r.user.id = :userId AND dtr.date = :date")
    List<DoseTakenRecord> findByUserIdAndDate(Integer userId, LocalDate date);
    
    /**
     * Find all dose taken records for a user in a date range
     */
    @Query("SELECT dtr FROM DoseTakenRecord dtr " +
            "JOIN dtr.dose d " +
            "JOIN d.reminder r " +
            "WHERE r.user.id = :userId AND dtr.date BETWEEN :startDate AND :endDate")
    List<DoseTakenRecord> findByUserIdAndDateBetween(Integer userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Count doses taken for a user on a specific date
     */
    @Query("SELECT COUNT(dtr) FROM DoseTakenRecord dtr " +
            "JOIN dtr.dose d " +
            "JOIN d.reminder r " +
            "WHERE r.user.id = :userId AND dtr.date = :date")
    long countByUserIdAndDate(Integer userId, LocalDate date);
}