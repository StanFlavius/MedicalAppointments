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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

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
        mockMvc.perform(get("/departments")
                        .flashAttr("department", Collections.singletonList(createDepartment())))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    public void showDepartmentInfo_unauthenticated_user_success() throws Exception {
        mockMvc.perform(get("/departments/{1}", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("department_info"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    @WithMockUser(username = "pacient_1", roles={"PATIENT"})
    void getAllByPatientUser_success() throws Exception {
        mockMvc.perform(get("/departments")
                        .flashAttr("department", Collections.singletonList(createDepartment())))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin_1", roles={"ADMIN"})
    void create_success_byAdmin() throws Exception {
        Department department = createDepartment();

        mockMvc.perform(post("/departments")
                        .flashAttr("department", department))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/departments"));
    }

    @Test
    @WithMockUser(username = "admin_1", roles={"ADMIN"})
    void showCreatePage_byAdmin() throws Exception {
        mockMvc.perform(get("/departments/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("department_form"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    @WithMockUser(username = "admin_1", roles={"ADMIN"})
    void create_notValidField_failure() throws Exception {
        Department department = createDepartment();
        department.setName("Cardiologie");

        mockMvc.perform(post("/departments")
                        .flashAttr("department", department))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/departments/new"))
                .andExpect(flash().attribute("error_department", "Department with name Cardiologie already exists!"));
    }

    @Test
    @WithMockUser(username = "admin_1", roles={"ADMIN"})
    void update_success_byAdmin() throws Exception {
        Department department = createDepartment();
        department.setId(1L);
        department.setName("Oftalmologie");

        mockMvc.perform(post("/departments")
                        .flashAttr("department", department))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/departments"));
    }

    @Test
    @WithMockUser(username = "admin_1", roles={"ADMIN"})
    void showUpdatePage_byAdmin() throws Exception {
        mockMvc.perform(get("/departments/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("department_form"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    @WithMockUser(username = "admin_1", roles={"ADMIN"})
    void update_notValidField_failure() throws Exception {
        Department department = createDepartment();
        department.setId(1L);
        department.setName("Cardiologie");

        mockMvc.perform(post("/departments")
                        .flashAttr("department", department))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/departments/1/edit"))
                .andExpect(flash().attribute("error_department", "Department with name Cardiologie already exists!"));
    }

    private Department createDepartment() {
        return Department.builder()
                .name("Stomatologie")
                .build();
    }
}
