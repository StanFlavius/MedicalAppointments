package com.example.medicalappointments.controller;

import com.example.medicalappointments.service.DoctorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test for {@link com.example.medicalappointments.controller.DoctorController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
public class DoctorControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    DoctorService doctorService;

    @Test
    @WithMockUser(username = "admin_1", password = "123456", roles = "ADMIN")
    public void showDoctors_admin_success() throws Exception {
        mockMvc.perform(get("/doctors"))
                .andExpect(status().isOk())
                .andExpect(view().name("doctors"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }
}