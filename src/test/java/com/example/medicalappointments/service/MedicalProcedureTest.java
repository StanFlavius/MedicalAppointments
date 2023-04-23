package com.example.medicalappointments.service;

import com.example.medicalappointments.model.Department;
import com.example.medicalappointments.model.MedicalProcedure;
import com.example.medicalappointments.repository.MedicalProcedureRepository;
import com.example.medicalappointments.service.interfaces.DepartmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MedicalProcedureTest {

    @Mock
    private MedicalProcedureRepository medicalProcedureRepository;

    @Mock
    private DepartmentService departmentService;

    @InjectMocks
    private MedicalProcedureService medicalProcedureService;

    @Test
    public void getProceduresByDepartment() {
        Department department = createDepartment();
        MedicalProcedure medicalProcedure = createProcedure(department);

        when(departmentService.getDepartmentById(10L)).thenReturn(department);
        when(medicalProcedureRepository.findByDepartment(any())).thenReturn(List.of(medicalProcedure));

        List<MedicalProcedure> resultedProcedures = medicalProcedureService.getProceduresByDepartment(Long.valueOf("10"));

        assertEquals(1, resultedProcedures.size());
        assertEquals(resultedProcedures.get(0).getId(), medicalProcedure.getId());
        assertEquals(resultedProcedures.get(0).getDepartment().getName(), medicalProcedure.getDepartment().getName());
        assertEquals(resultedProcedures.get(0).getName(), medicalProcedure.getName());
        assertEquals(resultedProcedures.get(0).getPrice(), medicalProcedure.getPrice());

        verify(medicalProcedureRepository, times(1)).findByDepartment(department);
        verify(departmentService, times(1)).getDepartmentById(10L);
    }

    @Test
    public void createProcedure() {
        Department department = createDepartment();
        MedicalProcedure medicalProcedure = createProcedure(department);

        when(departmentService.getDepartmentById(10L)).thenReturn(department);
        when(medicalProcedureRepository.save(any())).thenReturn(medicalProcedure);

        MedicalProcedure savedProcedure = medicalProcedureService.save(department.getId(), medicalProcedure);

        assertEquals("EKG", savedProcedure.getName());
        assertEquals(100L, savedProcedure.getPrice().longValue());
        assertEquals(10L, savedProcedure.getDepartment().getId());

        verify(departmentService, times(1)).getDepartmentById(10L);
        verify(medicalProcedureRepository, times(1)).save(medicalProcedure);
    }


    private Department createDepartment() {
        Department department = new Department();
        department.setId(10L);
        department.setName("Neurologie");

        return department;
    }

    private MedicalProcedure createProcedure(Department department) {
        MedicalProcedure medicalProcedure = new MedicalProcedure();
        medicalProcedure.setId(1L);
        medicalProcedure.setName("EKG");
        medicalProcedure.setPrice(100);
        medicalProcedure.setDepartment(department);

        return medicalProcedure;
    }
}
