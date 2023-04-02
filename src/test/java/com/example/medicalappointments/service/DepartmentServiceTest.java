package com.example.medicalappointments.service;

import com.example.medicalappointments.model.Department;
import com.example.medicalappointments.repository.DepartmentRepository;
import com.example.medicalappointments.service.interfaces.DepartmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private static final Long ID = 1L;

    @Test
    @DisplayName("Get all departments - success")
    void getAll_success() {
        when(departmentRepository.findAll()).thenReturn(Collections.singletonList(getSavedDepartment()));

        List<Department> departments = departmentService.getAllDepartments();

        assertNotNull(departments);
        assertEquals(1, departments.size());
        verify(departmentRepository, times(1)).findAll();
    }

    private Department createDepartment() {
        return Department.builder()
                .name("Neurologie")
                .build();
    }

    private Department getSavedDepartment() {
        Department savedDepartment = createDepartment();
        savedDepartment.setId(ID);

        return savedDepartment;
    }
}
