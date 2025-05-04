package com.zerobee.pillscheduler.entity;

import com.zerobee.pillscheduler.dto.UserDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    private String id;
    
    private String name;
    
    @Column(unique = true)
    private String email;
    
    private String photo_url;
    
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    private List<Reminder> reminders = new ArrayList<>();
    
    public UserDTO toUserDTO() {
        return UserDTO.builder()
                .id(id)
                .name(name)
                .email(email)
                .photo_url(photo_url)
                .build();
    }
}