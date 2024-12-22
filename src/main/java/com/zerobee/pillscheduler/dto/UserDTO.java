package com.zerobee.pillscheduler.dto;

import com.zerobee.pillscheduler.entity.User;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String id;
    private String name;
    private String email;
    private String photo_url;
    
    public User toUser() {
        return User.builder()
                .id(id)
                .name(name)
                .email(email)
                .photo_url(photo_url)
                .build();
    }
}
