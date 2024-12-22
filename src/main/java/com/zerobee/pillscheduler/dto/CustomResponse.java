package com.zerobee.pillscheduler.dto;


import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CustomResponse<T> {
    private HttpStatus status;
    private String message;
    private T data;
}