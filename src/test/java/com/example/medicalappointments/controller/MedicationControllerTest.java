package com.example.medicalappointments.controller;

import com.example.medicalappointments.model.Medication;
import com.example.medicalappointments.service.MedicationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test for {@link com.example.project.controller.MedicationController}
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
@Transactional
public class MedicationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private MedicationService medicationService;

    @Test
    @WithMockUser(username = "admin_1", roles={"ADMIN"})
    void getAllByAdminUser_success() throws Exception {
        Medication medication = new Medication();
        medication.setName("Cerebrozyl");
        medication.setQuantity(400);

        mockMvc.perform(get("/medications")
                        .flashAttr("medication", Collections.singletonList(medication)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin_1", password = "123456", roles={"ADMIN"})
    void showConsultFormPage_patient_success() throws Exception {
        mockMvc.perform(get("/medications/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("medication_form"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    @WithMockUser(username = "admin_1", roles={"ADMIN"})
    void create_success_byAdmin() throws Exception {
        Medication medication = new Medication();
        medication.setName("Cerebrozyl");
        medication.setQuantity(400);

        mockMvc.perform(post("/medications")
                        .flashAttr("medication", medication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/medications"));
    }

    @Test
    @WithMockUser(username = "admin", password = "1234", roles = "ADMIN")
    @DisplayName("Test that validation form has input errors")
    void addNewMedicationFailsValidation() throws Exception {

        Medication medication = new Medication();
        medication.setName("");
        medication.setQuantity(-2);
        mockMvc.perform(post("/medications").flashAttr("medication", medication))
                .andExpect(model().attributeHasFieldErrors("medication", "quantity"))
                .andExpect(model().attributeHasFieldErrors("medication", "name"))
                .andExpect(status().isOk())
                .andExpect(view().name("medication_form"));
    }

    @Test
    @WithMockUser(username = "admin", password = "1234", roles = "ADMIN")
    @DisplayName("Test that validation fails when already existing medication - quantity in database")
    void addNewMedication_Fails() throws Exception {

        Medication medication = new Medication();
        medication.setName("Controloc");
        medication.setQuantity(1000);

        mockMvc.perform(post("/medications").flashAttr("medication", medication))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/medications/new"))
                .andExpect(redirectedUrl("/medications/new"));

    }
}
