package com.upgrad.quora.service.exception;

public class QuoraException extends RuntimeException {
    private final String errorCode;

    public QuoraException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() { return errorCode; }
}
