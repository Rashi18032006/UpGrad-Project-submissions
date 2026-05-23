package com.upgrad.quora.service.exception;

public class UserNotFoundException extends QuoraException {
    public UserNotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }
}
