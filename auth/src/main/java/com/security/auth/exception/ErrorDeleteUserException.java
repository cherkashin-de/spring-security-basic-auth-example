package com.security.auth.exception;

public class ErrorDeleteUserException extends RuntimeException {
    public ErrorDeleteUserException(String message, Throwable cause) {
        super(message,  cause);
    }
}
