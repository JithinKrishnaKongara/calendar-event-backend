package com.example.calendar_backend.exception;

public class EventOverlapException extends RuntimeException {
    public EventOverlapException(String message) {
        super(message);
    }
}
