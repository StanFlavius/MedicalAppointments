package com.example.medicalappointments.service.interfaces;

import com.example.medicalappointments.model.Department;

import java.util.List;
import java.util.Optional;

public interface DepartmentService {

    List<Department> getAllDepartments();

    Department getDepartmentById(Long id);

    Optional<Department> getDepartmentByName(String departmentName);

    Department saveDepartment(Department department);

}
