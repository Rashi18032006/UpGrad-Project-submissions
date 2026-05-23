package com.upgrad.quora.service.exception;

public class AnswerNotFoundException extends QuoraException {
    public AnswerNotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }
}
