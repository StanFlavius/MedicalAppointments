package com.example.medicalappointments.controller;

import com.example.medicalappointments.model.Consult;
import com.example.medicalappointments.model.Doctor;
import com.example.medicalappointments.model.Patient;
import com.example.medicalappointments.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class ConsultControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "pacient_1", password = "123456", roles={"PATIENT"})
    void showConsultFormPage_patient_success() throws Exception {
        mockMvc.perform(get("/consults/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("consult_form"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    @WithMockUser(username = "pacient_1", password = "123456", roles={"PATIENT"})
    void createConsult_patient_success() throws Exception {
        Consult consult = createConsult();

        mockMvc.perform(post("/consults")
                        .flashAttr("consult", consult))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/consults"));
    }

    @Test
    @WithMockUser(username = "pacient_1", password = "123456", roles={"PATIENT"})
    void createConsult_doctorNotSelected_patient_failure() throws Exception {
        Consult consult = createConsult();
        consult.setDoctor(null);

        mockMvc.perform(post("/consults")
                        .flashAttr("consult", consult))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/consults/new"));
    }

    @Test
    @WithMockUser(username = "pacient_1", password = "123456", roles={"PATIENT"})
    void createConsult_pastDate_patient_failure() throws Exception {
        Consult consult = createConsult();
        consult.setDate(new Date(System.currentTimeMillis() - 1000));

        mockMvc.perform(post("/consults")
                        .flashAttr("consult", consult))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/consults/new"));
    }

    private Consult createConsult() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);

        Consult consult = new Consult();
        consult.setDoctor(doctor);
        consult.setDate(new Date(System.currentTimeMillis() + 99999));

        return consult;
    }
}