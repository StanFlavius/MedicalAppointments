package com.example.medicalappointments.controller;

import com.example.medicalappointments.model.Doctor;
import com.example.medicalappointments.model.User;
import com.example.medicalappointments.service.DoctorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test for {@link com.example.medicalappointments.controller.DoctorController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
@Transactional
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

    @Test
    @WithMockUser(username = "patient_1", password = "123456", roles = "PATIENT")
    public void showDoctors_patient_success() throws Exception {
        mockMvc.perform(get("/doctors"))
                .andExpect(status().isOk())
                .andExpect(view().name("doctors"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    @WithMockUser(username = "patient_1", password = "123456", roles = "PATIENT")
    public void showDoctorInfo_patient_success() throws Exception {
        mockMvc.perform(get("/doctors/{1}", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("doctor_info"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    @WithMockUser(username = "patient_1", password = "123456", roles = "PATIENT")
    public void showDoctorInfo_doctorNotFound_patient_error() throws Exception {
        mockMvc.perform(get("/doctors/{1}", "999999"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("err_not_found"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    @WithMockUser(username = "doctor_1", password = "123456", roles = "DOCTOR")
    public void editDoctor_POST_doctor_fail() throws Exception {
        mockMvc.perform(get("/doctors/1/edit"))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/access-denied"));
    }

    @Test
    @WithMockUser(username = "admin_1", password = "123456", roles = "ADMIN")
    public void editDoctor_POST_admin_success() throws Exception {

        Doctor doctor = doctorService.findById(1L);
        User user = doctor.getUser();

        user.setLastName("Mano");
        user.setFirstName("Andrei");
        user.setEmail("abc@email.com");

        mockMvc.perform(post("/doctors")
                        .flashAttr("doctor", doctor)
                        .flashAttr("user", user))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/doctors"));

        doctor = doctorService.findById(1L);
        assertEquals("Mano", doctor.getUser().getLastName());
        assertEquals("Andrei", doctor.getUser().getFirstName());
        assertEquals("abc@email.com", doctor.getUser().getEmail());
    }

    @Test
    @WithMockUser(username = "admin_1", password = "123456", roles = "ADMIN")
    public void editDoctor_POST_admin_fail() throws Exception {

        Doctor doctor = doctorService.findById(1L);
        User user = doctor.getUser();

        doctor.getUser().setLastName("1234567");
        doctor.getUser().setFirstName("98765");
        user.setUsername("");

        mockMvc.perform(post("/doctors")
                        .flashAttr("doctor", doctor)
                        .flashAttr("user", user))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/doctors/1/edit"));
    }

    @Test
    @WithMockUser(username = "admin_1", password = "123456", roles = "ADMIN")
    public void deleteDoctor_admin_success() throws Exception {
        mockMvc.perform(get("/doctors/{id}/delete", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/doctors"));
    }

    @Test
    @WithMockUser(username = "doctor_1", password = "123456", roles = "DOCTOR")
    public void deleteDoctor_doctor_fail() throws Exception {
        mockMvc.perform(get("/doctors/{id}/delete", "1"))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/access-denied"));
    }

    @Test
    @WithAnonymousUser
    public void deleteDoctor_anonymous_fail() throws Exception {
        mockMvc.perform(get("/doctors/{id}/delete", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }
}
