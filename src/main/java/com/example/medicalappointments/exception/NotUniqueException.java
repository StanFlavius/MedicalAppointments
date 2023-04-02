package com.example.medicalappointments.exception;

public class NotUniqueException extends RuntimeException {

    public NotUniqueException() {
        super();
    }

    public NotUniqueException(String message) {
        super(message);
    }
}
