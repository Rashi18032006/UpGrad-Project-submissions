package com.upgrad.quora.service.exception;

public class AuthorizationFailedException extends QuoraException {
    public AuthorizationFailedException(String errorCode, String message) {
        super(errorCode, message);
    }
}
