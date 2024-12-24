package com.zerobee.pillscheduler.controller;

import com.zerobee.pillscheduler.dto.AccessTokenBody;
import com.zerobee.pillscheduler.dto.CustomResponse;
import com.zerobee.pillscheduler.dto.UserDTO;
import com.zerobee.pillscheduler.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService service;
    
    @PostMapping("/login")
    public CustomResponse<UserDTO> loginUserHandler(@RequestBody AccessTokenBody tokenBody) {
        return new CustomResponse<>(
                HttpStatus.OK,
                "User Logged Successfully",
                service.loginUser(tokenBody)
        );
    }
    
    @GetMapping("/fetch")
    public CustomResponse<UserDTO> fetchUserHandler(
            @RequestHeader("Authorization") String token
    ){
        return new CustomResponse<>(
                HttpStatus.OK,
                "User Fetched Successfully.",
                service.fetchUser(token)
        );
    }
    
}
