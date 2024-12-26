package com.zerobee.pillscheduler.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.zerobee.pillscheduler.dto.ReminderDTO;
import com.zerobee.pillscheduler.entity.Dose;
import com.zerobee.pillscheduler.entity.Reminder;
import com.zerobee.pillscheduler.entity.User;
import com.zerobee.pillscheduler.enums.Flag;
import com.zerobee.pillscheduler.enums.Status;
import com.zerobee.pillscheduler.exception.ReminderNotFoundException;
import com.zerobee.pillscheduler.repository.ReminderRepository;
import com.zerobee.pillscheduler.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReminderService {
    
    private final UserRepository userRepository;
    private final ReminderRepository reminderRepository;
    
    public ReminderDTO createReminder(String token, ReminderDTO reminderDTO) {
        String userId = extractUserIdFromToken(token);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Reminder reminder = Reminder.builder()
                .r_name(reminderDTO.getR_name())
                .r_photo(reminderDTO.getR_photo())
                .r_type(reminderDTO.getR_type())
                .category(reminderDTO.getCategory())
                .frequency(reminderDTO.getFrequency())
                .start_date_time(reminderDTO.getStart_date_time())
                .end_date_time(reminderDTO.getEnd_date_time())
                .notes(reminderDTO.getNotes())
                .flag(Flag.ACTIVE)
                .status(Status.NOT_TAKEN)
                .remaining_doses(reminderDTO.getDoses().size())
                .user(user)
                .build();
        
        List<Dose> doses = reminderDTO.getDoses().stream().map(doseTime ->
                Dose.builder()
                        .doseTime(doseTime.getDoseTime())
                        .reminder(reminder)
                        .build()
        ).toList();
        
        reminder.setDoses(doses);
        
        Reminder savedReminder = reminderRepository.save(reminder);
        
        return savedReminder.toReminderDTO();
    }
    
    public List<ReminderDTO> getRemindersForUser(String token) {
        String userId = extractUserIdFromToken(token);
        
        return reminderRepository.findByUserId(userId).stream()
                .map(Reminder::toReminderDTO)
                .toList();
    }
    
    private String extractUserIdFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid token");
        }
        String idToken = token.substring(7); // Remove "Bearer " prefix
        
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            return decodedToken.getUid(); // This is the Firebase UID
        } catch (FirebaseAuthException e) {
            throw new IllegalArgumentException("Invalid Firebase ID Token", e);
        }
    }
    
    public void deleteReminderById(String token, Integer id) {
        String userId = extractUserIdFromToken(token);
        
        Reminder reminder = reminderRepository.findById(id)
                .filter(r -> r.getUser().getId().equals(userId))  // Ensure the reminder belongs to the user
                .orElseThrow(() -> new ReminderNotFoundException("Reminder with ID " + id + " not found or not belonging to the user."));
        
        reminderRepository.delete(reminder);
    }
    
    public void markDoseAsTaken(String token, Integer reminderId, Integer doseId) {
        String userId = extractUserIdFromToken(token);
        
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new ReminderNotFoundException("Reminder not found:" + reminderId));
        
        if (!reminder.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("This reminder does not belong to the user");
        }
        
        // Validate the dose exists
        Dose dose = reminder.getDoses().stream()
                .filter(d -> d.getId().equals(doseId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Dose not found"));
        
        int newRemainingDoses = reminder.getRemaining_doses() - 1;
        reminder.setRemaining_doses(Math.max(newRemainingDoses, 0));
        
        if (reminder.getRemaining_doses() == 0) {
            reminder.setStatus(Status.TAKEN);
        }
        
        reminderRepository.save(reminder);
    }
    
    
}
