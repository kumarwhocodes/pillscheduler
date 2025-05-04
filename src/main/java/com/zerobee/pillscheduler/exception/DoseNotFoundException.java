package com.zerobee.pillscheduler.exception;

public class DoseNotFoundException extends RuntimeException {
    public DoseNotFoundException(String message) {
        super(message);
    }
}
