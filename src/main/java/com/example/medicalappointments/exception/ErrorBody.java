package com.example.medicalappointments.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorBody {

    private String message;
}
