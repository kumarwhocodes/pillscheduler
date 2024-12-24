package com.zerobee.pillscheduler.exception;

public class TokenNotFound extends RuntimeException {
    public TokenNotFound() {
        super("No Token found in the request");
    }
}