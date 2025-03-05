package com.zerobee.pillscheduler.exception;

public class FirebaseOperationException extends RuntimeException {
    public FirebaseOperationException(String message) {
        super(message);
    }
}