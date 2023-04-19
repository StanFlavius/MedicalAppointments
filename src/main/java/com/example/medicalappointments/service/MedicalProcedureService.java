package com.example.medicalappointments.service;

import com.example.medicalappointments.model.Department;
import com.example.medicalappointments.model.MedicalProcedure;
import com.example.medicalappointments.repository.DepartmentRepository;
import com.example.medicalappointments.repository.MedicalProcedureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalProcedureService {

    private final MedicalProcedureRepository medicalProcedureRepository;
    private final DepartmentRepository departmentRepository;

    public List<MedicalProcedure> getProceduresByDepartment(Long id){
        Department department = departmentRepository.getById(id);

        return medicalProcedureRepository.findByDepartment(department);
    }
}
