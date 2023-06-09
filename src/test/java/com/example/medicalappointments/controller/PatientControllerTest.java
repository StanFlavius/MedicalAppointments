package com.example.medicalappointments.controller;

import com.example.medicalappointments.model.Patient;
import com.example.medicalappointments.model.User;
import com.example.medicalappointments.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
@Transactional
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientService patientService;

    @Test
    @WithMockUser(username = "admin_1", password = "123456", roles = "ADMIN")
    public void editPatient_POST_admin_success() throws Exception {

        Patient patient = patientService.findById(1L);
        User user = patient.getUser();

        user.setLastName("ABC");
        user.setFirstName("ABC");

        mockMvc.perform(post("/patients")
                .flashAttr("patient", patient)
                .flashAttr("user", user))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients"));

        patient = patientService.findById(1L);
        assertEquals("ABC", patient.getUser().getLastName());
        assertEquals("ABC", patient.getUser().getFirstName());
    }

    @Test
    @WithMockUser(username = "admin_1", password = "123456", roles = "ADMIN")
    public void editPatient_POST_admin_fail() throws Exception {

        Patient patient = patientService.findById(1L);
        User user = patient.getUser();

        patient.getUser().setLastName("ABC");
        patient.getUser().setFirstName("ABC");
        user.setUsername("");

        mockMvc.perform(post("/patients")
                .flashAttr("patient", patient)
                .flashAttr("user", user))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients/1/edit"));
    }

    @Test
    @WithMockUser(username = "admin_1", password = "123456", roles = "ADMIN")
    public void deletePatient_admin_success() throws Exception {
        mockMvc.perform(get("/patients/{id}/delete", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients"));
    }

    @Test
    @WithMockUser(username = "admin_1", password = "123456", roles = "ADMIN")
    public void showPatients_admin_success() throws Exception {
        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(view().name("patients"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    @WithMockUser(username = "doctor_1", password = "123456", roles = "DOCTOR")
    public void showPatients_doctor_success() throws Exception {
        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(view().name("patients"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    @WithMockUser(username = "doctor_1", password = "123456", roles = "DOCTOR")
    public void showPatientInfo_doctor_success() throws Exception {
        mockMvc.perform(get("/patients/{1}", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("patient_info"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    @WithMockUser(username = "admin_1", password = "123456", roles = "ADMIN")
    public void showPatientInfo_admin_success() throws Exception {
        mockMvc.perform(get("/patients/{1}", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("patient_info"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    @WithAnonymousUser
    void showRegisterPage_success() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    @WithAnonymousUser
    void register_success() throws Exception {
        Patient patient = createPatient();

        mockMvc.perform(post("/register")
                        .flashAttr("patient", patient))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithAnonymousUser
    void register_notValidFields_failure() throws Exception {
        Patient patient = new Patient();
        User user = new User();
        patient.setUser(user);

        mockMvc.perform(post("/register")
                        .flashAttr("patient", patient))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("patient", "cnp", "user.username",
                        "user.email", "user.password", "user.firstName", "user.lastName"));
    }

    private Patient createPatient() {
        User user = new User();
        user.setUsername("test-username");
        user.setPassword("test-pass");
        user.setEmail("test@gmail.com");
        user.setFirstName("testfn");
        user.setLastName("testln");

        Patient patient = new Patient();
        patient.setCnp("1234567891234");
        patient.setUser(user);

        return patient;
    }
}
