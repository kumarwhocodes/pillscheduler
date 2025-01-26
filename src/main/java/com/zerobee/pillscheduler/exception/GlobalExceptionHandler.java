package com.zerobee.pillscheduler.exception;

import com.zerobee.pillscheduler.dto.CustomResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(TokenNotFound.class)
    public ResponseEntity<CustomResponse<Void>> handleTokenNotFound(TokenNotFound ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new CustomResponse<>(HttpStatus.BAD_REQUEST, "Token Not Found", null));
    }
    
    @ExceptionHandler(InvalidToken.class)
    public ResponseEntity<CustomResponse<Void>> handleInvalidToken(InvalidToken ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new CustomResponse<>(HttpStatus.UNAUTHORIZED, "Invalid Token", null));
    }
    
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<CustomResponse<Void>> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new CustomResponse<>(
                        HttpStatus.NOT_FOUND,
                        "User Not Found",
                        null
                ));
    }
    
    @ExceptionHandler(ReminderNotFoundException.class)
    public ResponseEntity<CustomResponse<Void>> handleReminderNotFoundException(ReminderNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new CustomResponse<>(
                        HttpStatus.NOT_FOUND,
                        "Reminder Not Found",
                        null
                ));
    }
    
    // IllegalStateException handler
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<CustomResponse<String>> handleIllegalStateException(IllegalStateException ex) {
        return new ResponseEntity<>(
                new CustomResponse<>(HttpStatus.BAD_REQUEST, ex.getMessage(), "ERROR"),
                HttpStatus.BAD_REQUEST
        );
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CustomResponse<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(
                new CustomResponse<>(HttpStatus.BAD_REQUEST, ex.getMessage(), "ERROR"),
                HttpStatus.BAD_REQUEST
        );
    }
    
    // Handle NullPointerException
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<CustomResponse<String>> handleNullPointerException(NullPointerException ex) {
        return new ResponseEntity<>(
                new CustomResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "Null pointer exception occurred", "ERROR"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
    
    // Handle DataIntegrityViolationException for database issues
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CustomResponse<String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return new ResponseEntity<>(
                new CustomResponse<>(HttpStatus.CONFLICT, "Data integrity violation", "ERROR"),
                HttpStatus.CONFLICT
        );
    }
    
    // Generic Exception handler for unforeseen errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponse<String>> handleGenericException(Exception ex) {
        return new ResponseEntity<>(
                new CustomResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong.", "ERROR"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
