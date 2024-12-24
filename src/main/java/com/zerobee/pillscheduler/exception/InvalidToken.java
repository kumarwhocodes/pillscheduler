package com.zerobee.pillscheduler.exception;

public class InvalidToken extends RuntimeException {
    public InvalidToken() {
        super("Invalid Token passed please login again");
    }
}