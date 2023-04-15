package com.example.medicalappointments.service;

import com.example.medicalappointments.exception.CustomException;
import com.example.medicalappointments.exception.EntityNotFoundException;
import com.example.medicalappointments.model.Department;
import com.example.medicalappointments.repository.DepartmentRepository;
import com.example.medicalappointments.service.interfaces.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Optional<Department> getDepartmentByName(String departmentName) {
        return departmentRepository.findDepartmentByName(departmentName);
    }

    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> EntityNotFoundException.builder()
                        .entityId(id)
                        .entityType("Department")
                        .build()
                );
    }

    public Department saveDepartment(Department department) {

        Optional<Department> departmentByName = getDepartmentByName(department.getName());
        if (departmentByName.isPresent()) {
            throw new CustomException(String.format("Department with name %s already exists!", department.getName()));
        }

        return departmentRepository.save(department);
    }
}
