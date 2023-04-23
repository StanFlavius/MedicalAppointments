package com.example.medicalappointments.service;

import com.example.medicalappointments.exception.EntityNotFoundException;
import com.example.medicalappointments.model.Department;
import com.example.medicalappointments.model.MedicalProcedure;
import com.example.medicalappointments.repository.MedicalProcedureRepository;
import com.example.medicalappointments.service.interfaces.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalProcedureService {

    private final DepartmentService departmentService;
    private final MedicalProcedureRepository medicalProcedureRepository;

    public List<MedicalProcedure> getProceduresByDepartment(Long departmentId) {
        Department department = departmentService.getDepartmentById(departmentId);
        return medicalProcedureRepository.findByDepartment(department);
    }

    public MedicalProcedure findById(Long id) {
        return medicalProcedureRepository.findById(id)
                .orElseThrow(() -> EntityNotFoundException.builder()
                        .entityType("Procedure")
                        .build());
    }

    public MedicalProcedure save(Long departmentId, MedicalProcedure medicalProcedure) {
        Department department = departmentService.getDepartmentById(departmentId);
        medicalProcedure.setDepartment(department);
        return medicalProcedureRepository.save(medicalProcedure);
    }

    public void deleteById(Long id) {
        medicalProcedureRepository.deleteById(id);
    }
}
