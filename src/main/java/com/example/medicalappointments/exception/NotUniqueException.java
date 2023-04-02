package com.example.medicalappointments.exception;

import lombok.Getter;

@Getter
public class NotUniqueException extends RuntimeException {

    private final ConflictingField conflictingField;

    public NotUniqueException(ConflictingField conflictingField, String message) {
        super(message);
        this.conflictingField = conflictingField;
    }

    public enum ConflictingField {
        USERNAME,
        EMAIL,
        CNP
    }
}
