package com.example.medicalappointments.repository;

import com.example.medicalappointments.model.Department;
import com.example.medicalappointments.model.MedicalProcedure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalProcedureRepository extends JpaRepository<MedicalProcedure, Long> {

    List<MedicalProcedure> findByDepartment(Department department);
}
