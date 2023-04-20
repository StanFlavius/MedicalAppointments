package com.example.medicalappointments.service;

import com.example.medicalappointments.exception.CustomException;
import com.example.medicalappointments.model.Medication;
import com.example.medicalappointments.repository.MedicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MedicationServiceTest {
    @Mock
    private MedicationRepository medicationRepository;

    @InjectMocks
    private MedicationService medicationService;

    private static final Long ID = 1L;

    @Test
    void saveMedication_success() {
        Medication medication = createMedication();
        Medication persistedMedication = createPersistedMedication();

        when(medicationRepository.findMedicationByNameAndQuantity(medication.getName(), medication.getQuantity())).thenReturn(Optional.empty());
        when(medicationRepository.save(medication)).thenReturn(persistedMedication);

        Medication resultedMedication = medicationService.saveMedication(medication);

        assertNotNull(resultedMedication);
        assertEquals(persistedMedication.getId(), resultedMedication.getId());
        assertEquals(persistedMedication.getName(), resultedMedication.getName());
        assertEquals(persistedMedication.getQuantity(), resultedMedication.getQuantity());

        verify(medicationRepository, times(1)).findMedicationByNameAndQuantity(medication.getName(), medication.getQuantity());
        verify(medicationRepository, times(1)).save(medication);
    }

    @Test
    void saveMedication_notUniqueName_exception() {
        Medication medication = createMedication();

        when(medicationRepository.findMedicationByNameAndQuantity(medication.getName(), medication.getQuantity())).thenReturn(Optional.of(medication));

        CustomException exception = assertThrows(CustomException.class, () -> medicationService.saveMedication(medication));

        assertEquals(String.format("Medication %s with quantity %s already exists!", medication.getName(), medication.getQuantity()), exception.getMessage());
    }

    private Medication createMedication() {
        return Medication.builder()
                .name("Cerebrozyl")
                .quantity(400)
                .build();
    }

    private Medication createPersistedMedication() {
        return Medication.builder()
                .id(1L)
                .name("Cerebrozyl")
                .quantity(400)
                .build();
    }
}
