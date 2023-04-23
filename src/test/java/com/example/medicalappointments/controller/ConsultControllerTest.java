package com.example.medicalappointments.controller;

import com.example.medicalappointments.model.Consult;
import com.example.medicalappointments.model.Department;
import com.example.medicalappointments.model.Doctor;
import com.example.medicalappointments.model.Patient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Calendar;
import java.util.Date;

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
        Consult consult = createConsultAsPatient();

        mockMvc.perform(post("/consults")
                        .flashAttr("consult", consult))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/consults"));
    }

    @Test
    @WithMockUser(username = "pacient_1", password = "123456", roles={"PATIENT"})
    void createConsult_doctorNotSelected_patient_failure() throws Exception {
        Consult consult = createConsultAsPatient();
        consult.setDoctor(null);

        mockMvc.perform(post("/consults")
                        .flashAttr("consult", consult))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/consults/new"));
    }

    @Test
    @WithMockUser(username = "pacient_1", password = "123456", roles={"PATIENT"})
    void createConsult_pastDate_patient_failure() throws Exception {
        Consult consult = createConsultAsPatient();
        consult.setDate(new Date(System.currentTimeMillis() - 1000));

        mockMvc.perform(post("/consults")
                        .flashAttr("consult", consult))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/consults/new"));
    }

    @Test
    @WithMockUser(username = "pacient_1", password = "123456", roles={"PATIENT"})
    void createConsult_dateNotInWorkingHours_patient_failure() throws Exception {
        Consult consult = createConsultAsPatient();
        consult.getDate().setHours(22);

        mockMvc.perform(post("/consults")
                        .flashAttr("consult", consult))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/consults/new"));
    }

    @Test
    @WithMockUser(username = "pacient_1", password = "123456", roles = "PATIENT")
    public void showConsults_patient_success() throws Exception {
        mockMvc.perform(get("/consults"))
                .andExpect(status().isOk())
                .andExpect(view().name("consults"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    @WithMockUser(username = "pacient_1", password = "123456", roles = "PATIENT")
    public void showConsultInfo_patient_success() throws Exception {
        mockMvc.perform(get("/consults/{1}", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("consult_info"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    @WithMockUser(username = "pacient_1", password = "123456", roles = "PATIENT")
    public void showConsultInfo_consultNotFound_patient_error() throws Exception {
        mockMvc.perform(get("/consults/{1}", "999999"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("err_not_found"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    @WithMockUser(username = "doctor_1", password = "123456", roles = "DOCTOR")
    public void showConsults_doctor_success() throws Exception {
        mockMvc.perform(get("/consults"))
                .andExpect(status().isOk())
                .andExpect(view().name("consults"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    @WithMockUser(username = "doctor_1", password = "123456", roles = "DOCTOR")
    public void showConsultInfo_doctor_success() throws Exception {
        mockMvc.perform(get("/consults/{1}", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("consult_info"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    @WithMockUser(username = "doctor_1", password = "123456", roles = "DOCTOR")
    public void showConsultInfo_consultNotFound_doctor_error() throws Exception {
        mockMvc.perform(get("/consults/{1}", "999999"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("err_not_found"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    @WithMockUser(username = "admin_1", password = "123456", roles = "ADMIN")
    public void showConsults_admin_success() throws Exception {
        mockMvc.perform(get("/consults"))
                .andExpect(status().isOk())
                .andExpect(view().name("consults"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    @WithMockUser(username = "admin_1", password = "123456", roles = "ADMIN")
    public void showConsultInfo_admin_success() throws Exception {
        mockMvc.perform(get("/consults/{1}", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("consult_info"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    @WithMockUser(username = "admin_1", password = "123456", roles = "ADMIN")
    public void showConsultInfo_consultNotFound_admin_error() throws Exception {
        mockMvc.perform(get("/consults/{1}", "999999"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("err_not_found"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    @WithMockUser(username = "pacient_1", password = "123456", roles = "PATIENT")
    public void deleteConsult_patient_success() throws Exception {
        mockMvc.perform(get("/consults/{1}/delete", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/consults"));
    }

    @Test
    @WithMockUser(username = "doctor_1", password = "123456", roles = "DOCTOR")
    public void deleteConsult_doctor_success() throws Exception {
        mockMvc.perform(get("/consults/{1}/delete", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/consults"));
    }

    @Test
    @WithMockUser(username = "doctor_1", password = "123456", roles = "DOCTOR")
    public void deleteConsult_consultNotFound_doctor_error() throws Exception {
        mockMvc.perform(get("/consults/{1}/delete", "999999"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("err_not_found"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    @WithMockUser(username = "pacient_1", password = "123456", roles = "PATIENT")
    public void deleteConsult_consultNotFound_patient_error() throws Exception {
        mockMvc.perform(get("/consults/{1}/delete", "999999"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("err_not_found"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    @WithMockUser(username = "doctor_1", password = "123456", roles={"DOCTOR"})
    void showConsultFormPage_doctor_success() throws Exception {
        mockMvc.perform(get("/consults/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("consult_form"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    @WithMockUser(username = "doctor_1", password = "123456", roles={"DOCTOR"})
    void createConsult_doctor_success() throws Exception {
        Consult consult = createConsultAsDoctor();

        mockMvc.perform(post("/consults")
                        .flashAttr("consult", consult))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/consults"));
    }

    @Test
    @WithMockUser(username = "doctor_1", password = "123456", roles={"DOCTOR"})
    void createConsult_patientNotSelected_doctor_failure() throws Exception {
        Consult consult = createConsultAsDoctor();
        consult.setPatient(null);

        mockMvc.perform(post("/consults")
                        .flashAttr("consult", consult))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/consults/new"));
    }

    @Test
    @WithMockUser(username = "doctor_1", password = "123456", roles={"DOCTOR"})
    void createConsult_pastDate_doctor_failure() throws Exception {
        Consult consult = createConsultAsDoctor();
        consult.setDate(new Date(System.currentTimeMillis() - 1000));

        mockMvc.perform(post("/consults")
                        .flashAttr("consult", consult))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/consults/new"));
    }

    @Test
    @WithMockUser(username = "doctor_1", password = "123456", roles={"DOCTOR"})
    void createConsult_dateNotInWorkingHours_doctor_failure() throws Exception {
        Consult consult = createConsultAsDoctor();
        consult.getDate().setHours(22);

        mockMvc.perform(post("/consults")
                        .flashAttr("consult", consult))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/consults/new"));
    }

    @Test
    @WithMockUser(username = "pacient_1", password = "123456",roles={"PATIENT"})
    void showUpdatePage_byPatient() throws Exception {
        mockMvc.perform(get("/consults/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("consult_form"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    @WithMockUser(username = "pacient_1", password = "123456",roles={"PATIENT"})
    void update_success_byPatient() throws Exception {
        Consult consult = createConsultAsPatient();
        consult.setId(1L);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 2);
        Date date = calendar.getTime();
        date.setHours(14);
        consult.setDate(date);

        mockMvc.perform(post("/consults")
                        .flashAttr("consult", consult))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/consults"));
    }

    @Test
    @WithMockUser(username = "pacient_1", password = "123456",roles={"PATIENT"})
    void update_notValidField_failure_byPatient() throws Exception {
        Consult consult = createConsultAsPatient();
        consult.setId(1L);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 2);
        Date date = calendar.getTime();
        date.setHours(19);
        consult.setDate(date);

        mockMvc.perform(post("/consults")
                        .flashAttr("consult", consult))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/consults/1/edit"))
                .andExpect(flash().attribute("error_date", "The time must be in the working hours!"));
    }


    @Test
    @WithMockUser(username = "doctor_1", password = "123456",roles={"DOCTOR"})
    void showUpdatePage_bydoctor() throws Exception {
        mockMvc.perform(get("/consults/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("consult_form"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    @WithMockUser(username = "doctor_1", password = "123456",roles={"DOCTOR"})
    void update_success_byDoctor() throws Exception {
        Consult consult = createConsultAsDoctor();
        consult.setId(1L);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 2);
        Date date = calendar.getTime();
        date.setHours(16);
        consult.setDate(date);

        mockMvc.perform(post("/consults")
                        .flashAttr("consult", consult))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/consults"));
    }

    @Test
    @WithMockUser(username = "doctor_1", password = "123456",roles={"DOCTOR"})
    void update_notValidField_failure_byDoctor() throws Exception {
        Consult consult = createConsultAsDoctor();
        consult.setId(1L);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 2);
        Date date = calendar.getTime();
        date.setHours(19);
        consult.setDate(date);

        mockMvc.perform(post("/consults")
                        .flashAttr("consult", consult))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/consults/1/edit"))
                .andExpect(flash().attribute("error_date", "The time must be in the working hours!"));
    }

    private Consult createConsultAsPatient() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 1);
        Date date = calendar.getTime();
        date.setHours(18);
        Consult consult = new Consult();
        consult.setDoctor(doctor);
        consult.setDate(date);

        return consult;
    }

    private Consult createConsultAsDoctor() {
        Patient patient = new Patient();
        patient.setId(1L);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 2);
        Date date = calendar.getTime();
        date.setHours(18);

        Consult consult = new Consult();
        consult.setPatient(patient);
        consult.setDate(date);

        return consult;
    }
}