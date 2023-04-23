package com.example.medicalappointments.service;

import com.example.medicalappointments.exception.CustomException;
import com.example.medicalappointments.model.Department;
import com.example.medicalappointments.repository.DepartmentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    void savePatient_success() {
        Department department = createDepartment();
        Department persistedDepartment = createPersistedDepartment();

        when(departmentRepository.findDepartmentByName(department.getName())).thenReturn(Optional.empty());
        when(departmentRepository.save(department)).thenReturn(persistedDepartment);

        Department resultedDepartment = departmentService.saveDepartment(department);

        assertNotNull(resultedDepartment);
        assertEquals(persistedDepartment.getId(), resultedDepartment.getId());
        assertEquals(persistedDepartment.getName(), resultedDepartment.getName());

        verify(departmentRepository, times(1)).findDepartmentByName(department.getName());
        verify(departmentRepository, times(1)).save(department);
    }

    @Test
    void savePatient_notUniqueName_exception() {
        Department department1 = createPersistedDepartment();
        Department department2 = createPersistedDepartment();
        department2.setId(2L);

        when(departmentRepository.findDepartmentByName(department1.getName())).thenReturn(Optional.of(department1));

        CustomException exception = assertThrows(CustomException.class, () -> departmentService.saveDepartment(department2));

        assertEquals(String.format("Department with name %s already exists!", department2.getName()), exception.getMessage());
    }

    @Test
    void deletePatient_success() {
        Department persistedDepartment = createPersistedDepartment();

        doNothing().when(departmentRepository).deleteById(persistedDepartment.getId());

        departmentService.deleteDepartmentById(persistedDepartment.getId());

        verify(departmentRepository, times(1)).deleteById(persistedDepartment.getId());
    }

    private Department createDepartment() {
        return Department.builder()
                .name("Neurologie")
                .build();
    }

    private Department createPersistedDepartment() {
        return Department.builder()
                .id(1L)
                .name("Neurologie")
                .build();
    }

    private Department getSavedDepartment() {
        Department savedDepartment = createDepartment();
        savedDepartment.setId(ID);

        return savedDepartment;
    }
}
