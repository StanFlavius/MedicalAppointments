package com.example.medicalappointments.repository;

import com.example.medicalappointments.model.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long> {

    Optional<Medication> findMedicationByNameAndQuantity(String name, Integer quantity);

    List<Medication> findByIdIsIn(List<Long> ids);
}
