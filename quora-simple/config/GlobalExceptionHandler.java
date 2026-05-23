package com.upgrad.quora.api.exception;

import com.upgrad.quora.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    record ErrorResponse(String code, String message, Instant timestamp) {}

    @ExceptionHandler(SignUpRestrictedException.class)
    public ResponseEntity<ErrorResponse> handleSignup(SignUpRestrictedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getErrorCode(), ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(AuthorizationFailedException.class)
    public ResponseEntity<ErrorResponse> handleAuth(AuthorizationFailedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(ex.getErrorCode(), ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getErrorCode(), ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(InvalidQuestionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidQuestion(InvalidQuestionException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getErrorCode(), ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(AnswerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAnswerNotFound(AnswerNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getErrorCode(), ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("ERR-500", ex.getMessage(), Instant.now()));
    }
}
