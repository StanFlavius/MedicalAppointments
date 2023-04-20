package com.example.medicalappointments.service;

import com.example.medicalappointments.exception.CustomException;
import com.example.medicalappointments.exception.EntityNotFoundException;
import com.example.medicalappointments.model.Medication;
import com.example.medicalappointments.repository.MedicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MedicationService {

    private final MedicationRepository medicationRepository;

    public Medication saveMedication(Medication medication) {
        checkIfMedicationAlreadyExistsByNameAndQuantity(medication.getName(), medication.getQuantity());
        return medicationRepository.save(medication);
    }

    public Medication getMedicationById(Long id) {
        return medicationRepository.findById(id)
                .orElseThrow(() -> EntityNotFoundException.builder()
                        .entityId(id)
                        .entityType("Medication")
                        .build()
                );
    }

    private void checkIfMedicationAlreadyExistsByNameAndQuantity(String name, Integer quantity) {
        if (medicationRepository.findMedicationByNameAndQuantity(name, quantity).isPresent()) {
            throw new CustomException(String.format("Medication %s with quantity %s already exists!", name, quantity));
        }
    }
}
