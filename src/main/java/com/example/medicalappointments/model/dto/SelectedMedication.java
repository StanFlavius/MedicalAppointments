package com.example.medicalappointments.model.dto;

import com.example.medicalappointments.model.Medication;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SelectedMedication {

    private Medication medication;
    private boolean isPresent;
}
