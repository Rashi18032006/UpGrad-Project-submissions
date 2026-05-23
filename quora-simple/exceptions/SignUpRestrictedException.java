package com.upgrad.quora.service.exception;

public class SignUpRestrictedException extends QuoraException {
    public SignUpRestrictedException(String errorCode, String message) {
        super(errorCode, message);
    }
}
