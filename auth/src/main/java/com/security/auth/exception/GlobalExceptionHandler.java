package com.security.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ErrorCreateUserException.class)
    public ResponseEntity<?> handleErrorCreateUserException(ErrorCreateUserException e) {
        Map<String, String> error = new HashMap<>();

        error.put("time", LocalDateTime.now().toString());
        error.put("exception", e.getClass().getName());
        error.put("message", e.getMessage());
        error.put("stackTrace", Arrays.toString(e.getStackTrace()).substring(1, 5_000));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(ErrorCreateUserInformationException.class)
    public ResponseEntity<?> handleErrorCreateUserException(ErrorCreateUserInformationException e) {
        Map<String, String> error = new HashMap<>();

        error.put("time", LocalDateTime.now().toString());
        error.put("exception", e.getClass().getName());
        error.put("message", e.getMessage());
        error.put("stackTrace", Arrays.toString(e.getStackTrace()).substring(1, 5_000));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(ErrorDeleteUserException.class)
    public ResponseEntity<?> handleErrorCreateUserException(ErrorDeleteUserException e) {
        Map<String, String> error = new HashMap<>();

        error.put("time", LocalDateTime.now().toString());
        error.put("exception", e.getClass().getName());
        error.put("message", e.getMessage());
        error.put("stackTrace", Arrays.toString(e.getStackTrace()).substring(1, 5_000));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        Map<String, String> error = new HashMap<>();

        error.put("time", LocalDateTime.now().toString());
        error.put("exception", e.getClass().getName());
        error.put("message", e.getMessage());
        error.put("stackTrace", Arrays.toString(e.getStackTrace()).substring(1, 5_000));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleException(RuntimeException e) {
        Map<String, String> error = new HashMap<>();
        error.put("time", LocalDateTime.now().toString());
        error.put("exception", e.getClass().getName());
        error.put("message", e.getMessage());
        error.put("cause", e.getCause().getClass().getName());
        error.put("stackTrace", Arrays.toString(e.getStackTrace()).substring(1, 5_000));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

}
