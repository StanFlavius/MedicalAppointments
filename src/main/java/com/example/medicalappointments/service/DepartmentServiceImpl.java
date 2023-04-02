package com.example.medicalappointments.service;

import com.example.medicalappointments.model.Department;
import com.example.medicalappointments.repository.DepartmentRepository;
import com.example.medicalappointments.service.interfaces.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }
}
