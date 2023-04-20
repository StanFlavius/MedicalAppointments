package com.example.medicalappointments.service;

import com.example.medicalappointments.exception.CustomException;
import com.example.medicalappointments.model.*;
import com.example.medicalappointments.repository.ConsultRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static com.example.medicalappointments.configuration.SecurityConfiguration.ROLE_PATIENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultServiceTest {

    @Mock
    private ConsultRepository consultRepository;

    @Mock
    private PatientService patientService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ConsultService consultService;

    @Test
    void createConsult_success() {
        Consult consult = createConsult();
        Patient persistedPatient = createPersistedPatient();
        Consult persistedConsult = createPersistedConsult(persistedPatient);

        when(userService.getCurrentUser()).thenReturn(persistedPatient.getUser());
        when(patientService.findByUserId(persistedPatient.getUser().getId())).thenReturn(persistedPatient);
        when(consultRepository.findConsultsInTimeRange(any(), any(), any())).thenReturn(Collections.emptyList());
        when(consultRepository.save(consult)).thenReturn(persistedConsult);

        Consult resultedConsult = consultService.saveConsult(consult);

        assertEquals(persistedConsult.getId(), resultedConsult.getId());
        assertEquals(persistedPatient, consult.getPatient());

        verify(consultRepository, times(1)).save(consult);
    }

    @Test
    void createConsult_pastDate_exception() {
        Consult consult = createConsult();
        consult.setDate(new Date(System.currentTimeMillis() - 1000));

        assertThrows(CustomException.class, () -> consultService.saveConsult(consult));
    }

    @Test
    void createConsult_dateOverlapsWithOtherConsults_exception() {
        Consult consult = createConsult();

        when(consultRepository.findConsultsInTimeRange(any(), any(), any())).thenReturn(Collections.singletonList(new Consult()));

        assertThrows(CustomException.class, () -> consultService.saveConsult(consult));
    }

    @Test
    void createConsult_dateNotInWorkingHours_exception() {
        Consult consult = createConsult();
        consult.getDate().setHours(22);

        assertThrows(CustomException.class, () -> consultService.saveConsult(consult));
    }

    private Consult createConsult() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 1);
        Date date = calendar.getTime();
        date.setHours(18);

        Consult consult = new Consult();
        consult.setDate(date);
        consult.setDoctor(new Doctor());
        return consult;
    }

    private Consult createPersistedConsult(Patient patient) {
        Consult consult = createConsult();
        consult.setPatient(patient);
        return consult;
    }

    private Patient createPatient() {
        User user = new User();
        user.setUsername("test-username");
        user.setPassword("test-pass");

        Patient patient = new Patient();
        patient.setCnp("1234567891234");
        patient.setUser(user);

        return patient;
    }

    private Role createPatientRole() {
        Role patientRole = new Role();
        patientRole.setName(ROLE_PATIENT);
        return patientRole;
    }

    private Patient createPersistedPatient() {
        Patient patient = createPatient();
        patient.setId(1L);
        patient.getUser().setPassword("enc_pass");
        patient.getUser().getRoles().add(createPatientRole());
        return patient;
    }

}