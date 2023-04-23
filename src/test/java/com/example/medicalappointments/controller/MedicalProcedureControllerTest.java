package com.example.medicalappointments.controller;

import com.example.medicalappointments.model.Department;
import com.example.medicalappointments.model.MedicalProcedure;
import com.example.medicalappointments.service.interfaces.DepartmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test for {@link com.example.medicalappointments.controller.MedicalProcedureController
 */

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
@Transactional
public class MedicalProcedureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DepartmentService departmentService;

    @Test
    @WithMockUser(username = "admin_1", roles = "ADMIN")
    void addProcedureAdmin_success() throws Exception {
        MedicalProcedure procedure = createProcedure();

        mockMvc.perform(post("/procedures/{id}", procedure.getDepartment().getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/departments/" + procedure.getDepartment().getId() + "/edit"));
    }

    @Test
    @WithMockUser(username = "doctor_1", roles = "DOCTOR")
    void addProcedureByDoctor_fail() throws Exception {
        MedicalProcedure procedure = createProcedure();

        mockMvc.perform(post("/procedures/{id}", procedure.getDepartment().getId()))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/access-denied"));
    }

    @Test
    @WithMockUser(username = "patient_1", roles = "PATIENT")
    void addProcedureByPatient_fail() throws Exception {
        MedicalProcedure procedure = createProcedure();

        mockMvc.perform(post("/procedures/{id}", procedure.getDepartment().getId()))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/access-denied"));
    }

    @Test
    @WithAnonymousUser
    void addProcedureByAnonymous_fail() throws Exception {
        MedicalProcedure procedure = createProcedure();

        mockMvc.perform(post("/procedures/{id}", procedure.getDepartment().getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "admin_1", roles = "ADMIN")
    void deleteProcedureAdmin_success() throws Exception {
        Department department = departmentService.getDepartmentById(1L);

        mockMvc.perform(get("/procedures/{id}/delete", department.getProcedures().get(0).getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/departments/" + department.getId() + "/edit"));
    }

    @Test
    @WithMockUser(username = "doctor_1", roles = "DOCTOR")
    void deleteProcedureByDoctor_fail() throws Exception {
        mockMvc.perform(get("/procedures/{id}/delete", 1))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/access-denied"));
    }

    @Test
    @WithMockUser(username = "patient_1", roles = "PATIENT")
    void deleteProcedureByPatient_fail() throws Exception {
        mockMvc.perform(get("/procedures/{id}/delete", 1))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/access-denied"));
    }

    @Test
    @WithAnonymousUser
    void deleteProcedureByAnonymous_fail() throws Exception {
        mockMvc.perform(get("/procedures/{id}/delete", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    private MedicalProcedure createProcedure() {
        Department department = departmentService.getDepartmentById(1L);
        return MedicalProcedure.builder()
                .name("Procedura")
                .price(100)
                .department(department)
                .build();
    }
}
