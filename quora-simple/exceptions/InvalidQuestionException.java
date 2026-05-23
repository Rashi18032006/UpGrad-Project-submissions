package com.upgrad.quora.service.exception;

public class InvalidQuestionException extends QuoraException {
    public InvalidQuestionException(String errorCode, String message) {
        super(errorCode, message);
    }
}
