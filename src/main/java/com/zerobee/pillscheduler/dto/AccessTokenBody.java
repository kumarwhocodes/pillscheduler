package com.zerobee.pillscheduler.dto;

import lombok.*;


@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenBody {
    private String token;
}