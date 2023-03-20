package com.example.medicalappointments.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class EntityNotFoundException extends RuntimeException {

    private final String entityType;

    private final Long entityId;
}
