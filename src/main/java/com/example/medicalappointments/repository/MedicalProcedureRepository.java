package com.example.medicalappointments.repository;

import com.example.medicalappointments.model.MedicalProcedure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalProcedureRepository extends JpaRepository<MedicalProcedure, Long> {
}
