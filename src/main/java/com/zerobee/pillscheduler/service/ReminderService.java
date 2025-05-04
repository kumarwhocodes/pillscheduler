package com.zerobee.pillscheduler.service;

import com.zerobee.pillscheduler.dto.DailyReminderSummaryDTO;
import com.zerobee.pillscheduler.dto.DoseDTO;
import com.zerobee.pillscheduler.dto.ReminderDTO;
import com.zerobee.pillscheduler.entity.Dose;
import com.zerobee.pillscheduler.entity.DoseTakenRecord;
import com.zerobee.pillscheduler.entity.Reminder;
import com.zerobee.pillscheduler.entity.User;
import com.zerobee.pillscheduler.enums.Flag;
import com.zerobee.pillscheduler.enums.Frequency;
import com.zerobee.pillscheduler.enums.Status;
import com.zerobee.pillscheduler.exception.DoseNotFoundException;
import com.zerobee.pillscheduler.exception.ReminderNotFoundException;
import com.zerobee.pillscheduler.exception.UserNotFoundException;
import com.zerobee.pillscheduler.repository.DoseRepository;
import com.zerobee.pillscheduler.repository.DoseTakenRecordRepository;
import com.zerobee.pillscheduler.repository.ReminderRepository;
import com.zerobee.pillscheduler.utils.FrequencyLogic;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ReminderService implements FrequencyLogic {
    
    private final ReminderRepository reminderRepository;
    private final DoseRepository doseRepository;
    private final DoseTakenRecordRepository doseTakenRecordRepository;
    private final UserService userService;
    
    @Transactional
    public ReminderDTO createReminder(String token, ReminderDTO requestDTO) {
        User user = userService.fetchUser(token).toUser();
        
        Reminder reminder = buildReminderEntity(requestDTO, user);
        reminder = reminderRepository.save(reminder);
        
        // Create doses based on the times specified in the DTO
        if (requestDTO.getDoses() != null && !requestDTO.getDoses().isEmpty()) {
            // Store the final reference to the reminder that we'll use in the lambda
            final Reminder finalReminder = reminder;
            
            List<Dose> doses = requestDTO.getDoses().stream()
                    .map(doseDTO -> {
                        // Use the final reference
                        return Dose.builder()
                                .doseTime(doseDTO.getDoseTime())
                                .taken(false)
                                .reminder(finalReminder) // Use the final reference
                                .build();
                    })
                    .collect(Collectors.toList());
            
            // Update the original reminder with the doses
            reminder.setDoses(doses);
            reminder.setRemaining_doses(doses.size());
            reminder = reminderRepository.save(reminder);
        }
        
        return enrichReminderDTOWithStatus(reminder.toReminderDTO(), LocalDate.now());
    }
    
    public List<ReminderDTO> fetchRemindersForUser(String token) {
        User user = userService.fetchUser(token).toUser();
        List<Reminder> reminders = reminderRepository.findByUserId(user.getId());
        
        LocalDate today = LocalDate.now();
        return reminders.stream()
                .map(reminder -> enrichReminderDTOWithStatus(reminder.toReminderDTO(), today))
                .collect(Collectors.toList());
    }
    
    public ReminderDTO fetchReminderById(String token, Integer id) {
        User user = userService.fetchUser(token).toUser();
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new ReminderNotFoundException("Reminder not found with ID: " + id));
        
        // Check if the reminder belongs to the authenticated user
        if (!reminder.getUser().getId().equals(user.getId())) {
            throw new UserNotFoundException("You are not authorized to access this reminder");
        }
        
        return enrichReminderDTOWithStatus(reminder.toReminderDTO(), LocalDate.now());
    }
    
    public List<ReminderDTO> fetchActiveRemindersByFrequency(String token, String flag, String status, Frequency frequency) {
        User user = userService.fetchUser(token).toUser();
        
        Flag flagEnum = Flag.valueOf(flag.toUpperCase());
        Status statusEnum = Status.valueOf(status.toUpperCase());
        
        List<Reminder> reminders = reminderRepository.findByUserAndFlagAndStatusAndFrequency(
                user, flagEnum, statusEnum, frequency);
        
        LocalDate today = LocalDate.now();
        return reminders.stream()
                .map(reminder -> enrichReminderDTOWithStatus(reminder.toReminderDTO(), today))
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void deleteReminderById(String token, Integer id) {
        User user = userService.fetchUser(token).toUser();
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new ReminderNotFoundException("Reminder not found with ID: " + id));
        
        // Check if the reminder belongs to the authenticated user
        if (!reminder.getUser().getId().equals(user.getId())) {
            throw new UserNotFoundException("You are not authorized to delete this reminder");
        }
        
        reminderRepository.delete(reminder);
    }
    
    @Transactional
    public void markDoseAsTakenForDate(String token, Integer reminderId, Integer doseId, LocalDate date) {
        User user = userService.fetchUser(token).toUser();
        Reminder reminder = getReminderForUser(reminderId, user);
        Dose dose = getDoseForReminder(doseId, reminder);
        
        // Check if dose is already marked as taken for this date
        if (doseTakenRecordRepository.existsByDoseAndDate(dose, date)) {
            log.info("Dose {} already marked as taken for date {}", doseId, date);
            return;
        }
        
        // Create a dose taken record
        DoseTakenRecord takenRecord = DoseTakenRecord.builder()
                .dose(dose)
                .date(date)
                .build();
        
        doseTakenRecordRepository.save(takenRecord);
        log.info("Dose {} marked as taken for date {}", doseId, date);
    }
    
    @Transactional
    public void markDoseAsNotTakenForDate(String token, Integer reminderId, Integer doseId, LocalDate date) {
        User user = userService.fetchUser(token).toUser();
        Reminder reminder = getReminderForUser(reminderId, user);
        Dose dose = getDoseForReminder(doseId, reminder);
        
        // Find and delete the dose taken record if it exists
        doseTakenRecordRepository.findByDoseAndDate(dose, date)
                .ifPresent(doseTakenRecordRepository::delete);
        
        log.info("Dose {} marked as not taken for date {}", doseId, date);
    }
    
    /**
     * New method to fetch reminders for a specific date
     */
    public List<ReminderDTO> fetchRemindersForDate(String token, LocalDate date) {
        User user = userService.fetchUser(token).toUser();
        List<Reminder> reminders = reminderRepository.findByUserId(user.getId());
        
        // Filter reminders applicable for the given date
        List<Reminder> remindersForDate = reminders.stream()
                .filter(reminder -> isReminderApplicableForDate(reminder, date))
                .toList();
        
        // Convert to DTOs with status for the specific date
        return remindersForDate.stream()
                .map(reminder -> enrichReminderDTOWithStatus(reminder.toReminderDTO(), date))
                .collect(Collectors.toList());
    }
    
    /**
     * New method to fetch reminder history for a date range
     */
    public List<DailyReminderSummaryDTO> fetchReminderHistory(String token, LocalDate startDate, LocalDate endDate) {
        // Validate date range
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        
        User user = userService.fetchUser(token).toUser();
        List<DailyReminderSummaryDTO> history = new ArrayList<>();
        
        // For each day in the range, generate a summary
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            history.add(getDailySummary(user, currentDate));
            currentDate = currentDate.plusDays(1);
        }
        
        return history;
    }
    
    /**
     * New method to get a summary for a specific day
     */
    public DailyReminderSummaryDTO getDailySummary(String token, LocalDate date) {
        User user = userService.fetchUser(token).toUser();
        return getDailySummary(user, date);
    }
    
    // Private helper methods
    
    private DailyReminderSummaryDTO getDailySummary(User user, LocalDate date) {
        List<Reminder> reminders = reminderRepository.findByUserId(user.getId()).stream()
                .filter(reminder -> isReminderApplicableForDate(reminder, date))
                .toList();
        
        int totalReminders = reminders.size();
        int totalDoses = 0;
        int dosesTaken = 0;
        
        // Create reminder statuses list
        List<DailyReminderSummaryDTO.DailyReminderStatusDTO> reminderStatuses = new ArrayList<>();
        
        for (Reminder reminder : reminders) {
            List<Dose> doses = reminder.getDoses();
            int reminderTotalDoses = doses.size();
            totalDoses += reminderTotalDoses;
            
            List<DailyReminderSummaryDTO.DoseDailyStatusDTO> doseStatuses = new ArrayList<>();
            int reminderDosesTaken = 0;
            
            for (Dose dose : doses) {
                boolean isDoseTaken = doseTakenRecordRepository.existsByDoseAndDate(dose, date);
                if (isDoseTaken) {
                    reminderDosesTaken++;
                    dosesTaken++;
                }
                
                // Add dose status
                doseStatuses.add(DailyReminderSummaryDTO.DoseDailyStatusDTO.builder()
                        .doseId(dose.getId())
                        .doseTime(dose.getDoseTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                        .taken(isDoseTaken)
                        .build());
            }
            
            // Add reminder status
            reminderStatuses.add(DailyReminderSummaryDTO.DailyReminderStatusDTO.builder()
                    .reminderId(reminder.getId())
                    .reminderName(reminder.getR_name())
                    .totalDoses(reminderTotalDoses)
                    .dosesTaken(reminderDosesTaken)
                    .doseStatuses(doseStatuses)
                    .build());
        }
        
        // Calculate adherence percentage
        double adherencePercentage = totalDoses > 0 ? ((double) dosesTaken / totalDoses) * 100 : 0;
        
        // Create summary DTO
        return DailyReminderSummaryDTO.builder()
                .date(date)
                .totalReminders(totalReminders)
                .totalDoses(totalDoses)
                .dosesTaken(dosesTaken)
                .dosesMissed(totalDoses - dosesTaken)
                .adherencePercentage(Math.round(adherencePercentage * 100.0) / 100.0) // Round to 2 decimal places
                .reminderStatuses(reminderStatuses)
                .build();
    }
    
    private boolean isReminderApplicableForDate(Reminder reminder, LocalDate date) {
        // Check if the date is within the reminder's date range
        LocalDate startDate = reminder.getStart_date_time().toLocalDate();
        LocalDate endDate = reminder.getEnd_date_time() != null ?
                reminder.getEnd_date_time().toLocalDate() : LocalDate.MAX;
        
        if (date.isBefore(startDate) || date.isAfter(endDate)) {
            return false;
        }
        
        // Check based on frequency
        return switch (reminder.getFrequency()) {
            case DAILY -> true;
            case ALTERNATE_DAYS -> {
                long daysBetween = startDate.until(date).getDays();
                yield daysBetween % 2 == 0;
            }
            case CUSTOM -> {
                DayOfWeek dayOfWeek = date.getDayOfWeek();
                yield isCustomDayReminder(reminder, dayOfWeek);
            }
            default -> false;
        };
    }
    
    private Reminder buildReminderEntity(ReminderDTO dto, User user) {
        return Reminder.builder()
                .r_name(dto.getR_name())
                .r_photo(dto.getR_photo())
                .r_type(dto.getR_type())
                .category(dto.getCategory())
                .frequency(dto.getFrequency())
                .days(dto.getDays())
                .start_date_time(dto.getStart_date_time())
                .end_date_time(dto.getEnd_date_time())
                .notes(dto.getNotes())
                .flag(dto.getFlag() != null ? dto.getFlag() : Flag.ACTIVE)
                .status(dto.getStatus() != null ? dto.getStatus() : Status.NOT_TAKEN)
                .remaining_doses(0) // Will be set after doses are created
                .user(user)
                .build();
    }
    
    private Reminder getReminderForUser(Integer reminderId, User user) {
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new ReminderNotFoundException("Reminder not found with ID: " + reminderId));
        
        if (!reminder.getUser().getId().equals(user.getId())) {
            throw new UserNotFoundException("You are not authorized to access this reminder");
        }
        
        return reminder;
    }
    
    private Dose getDoseForReminder(Integer doseId, Reminder reminder) {
        Dose dose = doseRepository.findById(doseId)
                .orElseThrow(() -> new DoseNotFoundException("Dose not found with ID: " + doseId));
        
        if (!dose.getReminder().getId().equals(reminder.getId())) {
            throw new ReminderNotFoundException("Dose does not belong to the specified reminder");
        }
        
        return dose;
    }
    
    /**
     * Renamed and simplified version of enrichReminderDTOWithTakenDates
     * This method enriches a ReminderDTO with the taken status for each dose on a specific date
     */
    private ReminderDTO enrichReminderDTOWithStatus(ReminderDTO reminderDTO, LocalDate date) {
        // For each dose in the DTO, check if it was taken on the specified date
        if (reminderDTO.getDoses() != null) {
            for (DoseDTO doseDTO : reminderDTO.getDoses()) {
                // Load the Dose entity to check its taken records
                Dose dose = doseRepository.findById(doseDTO.getId())
                        .orElseThrow(() -> new DoseNotFoundException("Dose not found with ID: " + doseDTO.getId()));
                
                // Check if the dose was taken on the specified date
                boolean wasTakenOnDate = doseTakenRecordRepository.existsByDoseAndDate(dose, date);
                doseDTO.setTaken(wasTakenOnDate);
                
                // Optional: If you want to include the full history of taken dates
                List<LocalDate> takenDates = doseTakenRecordRepository.findByDose(dose)
                        .stream()
                        .map(DoseTakenRecord::getDate)
                        .collect(Collectors.toList());
                
                doseDTO.setTakenDates(takenDates);
            }
        }
        
        return reminderDTO;
    }
}