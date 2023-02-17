package ru.kalita.ratelimit.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.kalita.ratelimit.exceptions.RateLimitExceededException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<String> limitReachedException(RateLimitExceededException exception) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(exception.getMessage());
    }
}
