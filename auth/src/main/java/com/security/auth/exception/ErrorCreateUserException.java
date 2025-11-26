package com.security.auth.exception;

public class ErrorCreateUserException extends RuntimeException {
    public ErrorCreateUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
