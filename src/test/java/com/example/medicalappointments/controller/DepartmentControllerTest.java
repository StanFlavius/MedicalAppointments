package com.example.medicalappointments.controller;

import com.example.medicalappointments.model.Department;
import com.example.medicalappointments.service.DepartmentServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
public class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    DepartmentServiceImpl departmentService;

    @Test
    @WithAnonymousUser
    void getAllByAnonymousUser_success() throws Exception {
        mockMvc.perform(get("http://localhost:8070/departments")
                        .flashAttr("department", Collections.singletonList(createDepartment())))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "pacient_1", roles={"PATIENT"})
    void getAllByPatientUser_success() throws Exception {
        mockMvc.perform(get("http://localhost:8070/departments")
                        .flashAttr("department", Collections.singletonList(createDepartment())))
                .andExpect(status().isOk());
    }

    private Department createDepartment() {
        return Department.builder()
                .name("Neurologie")
                .build();
    }
}
