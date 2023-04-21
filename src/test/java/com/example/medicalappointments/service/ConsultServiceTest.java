package com.example.medicalappointments.service;

import com.example.medicalappointments.exception.CustomException;
import com.example.medicalappointments.exception.EntityNotFoundException;
import com.example.medicalappointments.model.*;
import com.example.medicalappointments.repository.ConsultRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static com.example.medicalappointments.configuration.SecurityConfiguration.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultServiceTest {

    @Mock
    private ConsultRepository consultRepository;

    @Mock
    private PatientService patientService;

    @Mock
    private DoctorService doctorService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ConsultService consultService;

    @Test
    void createConsult_patient_success() {
        Consult consult = createConsult();
        Patient persistedPatient = createPersistedPatient();
        Consult persistedConsult = createPersistedConsult();
        persistedConsult.setPatient(persistedPatient);

        when(userService.hasRole(ROLE_DOCTOR)).thenReturn(false);
        when(userService.hasRole(ROLE_PATIENT)).thenReturn(true);
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
    void createConsult_doctor_success() {
        Consult consult = createConsult();
        Doctor persistedDoctor = createPersistedDoctor();
        Consult persistedConsult = createPersistedConsult();
        persistedConsult.setDoctor(persistedDoctor);

        when(userService.hasRole(ROLE_DOCTOR)).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(persistedDoctor.getUser());
        when(doctorService.findByUserId(persistedDoctor.getUser().getId())).thenReturn(persistedDoctor);
        when(consultRepository.findConsultsInTimeRange(any(), any(), any())).thenReturn(Collections.emptyList());
        when(consultRepository.save(consult)).thenReturn(persistedConsult);

        Consult resultedConsult = consultService.saveConsult(consult);

        assertEquals(persistedConsult.getId(), resultedConsult.getId());
        assertEquals(persistedDoctor, consult.getDoctor());

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

    @Test
    void getAllConsults_patient_success() {
        Patient patient = createPersistedPatient();
        Consult consult = createPersistedConsult();
        consult.setPatient(patient);

        when(userService.getCurrentUser()).thenReturn(User.builder().roles(patient.getUser().getRoles()).build());
        when(patientService.findByUserId(userService.getCurrentUser().getId())).thenReturn(patient);
        when(consultRepository.findAllByPatient_Id(patient.getId())).thenReturn(List.of(consult));

        List<Consult> resultedConsults = consultService.getAllConsults();

        assertEquals(1, resultedConsults.size());
        assertEquals(resultedConsults.get(0).getId(), consult.getId());
        assertEquals(resultedConsults.get(0).getPatient(), patient);
        assertTrue(resultedConsults.get(0).getPatient().getUser().getRoles().containsAll(patient.getUser().getRoles()));

        verify(consultRepository, times(1)).findAllByPatient_Id(patient.getId());
        verify(consultRepository, never()).findAll();
    }

    @Test
    void getAllConsults_admin_success() {
        Patient patient = createPersistedPatient();
        Consult consult = createPersistedConsult();
        consult.setPatient(patient);

        when(userService.getCurrentUser()).thenReturn(User.builder().roles(Set.of(createAdminRole())).build());
        when(consultRepository.findAll()).thenReturn(List.of(consult));

        List<Consult> resultedConsults = consultService.getAllConsults();

        assertEquals(1, resultedConsults.size());
        assertEquals(resultedConsults.get(0).getId(), consult.getId());
        assertEquals(resultedConsults.get(0).getPatient(), patient);

        verify(consultRepository, never()).findAllByPatient_Id(any());
        verify(consultRepository, times(1)).findAll();
    }

    @Test
    void getConsultById_success() {
        Patient patient = createPersistedPatient();
        Consult consult = createPersistedConsult();
        consult.setPatient(patient);

        when(consultRepository.findById(consult.getId())).thenReturn(Optional.of(consult));

        Consult resultedConsult = consultService.getConsultById(consult.getId());

        assertEquals(resultedConsult.getId(), consult.getId());
        assertEquals(resultedConsult.getPatient().getId(), patient.getId());

        verify(consultRepository, times(1)).findById(consult.getId());
    }

    @Test
    void getConsultById_consultNotFound_exception() {
        Long nonexistentConsultId = 1L;

        when(consultRepository.findById(nonexistentConsultId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> consultService.getConsultById(nonexistentConsultId));
    }

    @Test
    public void deleteConsult_success() {
        Consult consult = createPersistedConsult();

        when(consultRepository.findById(consult.getId())).thenReturn(Optional.of(consult));
        doNothing().when(consultRepository).delete(consult);

        consultService.deleteConsultById(consult.getId());

        verify(consultRepository, times(1)).delete(consult);
    }

    @Test
    public void deleteConsult_consultNotFound_exception() {
        Long nonexistentConsultId = 1L;

        when(consultRepository.findById(nonexistentConsultId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> consultService.deleteConsultById(nonexistentConsultId));
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

    private Consult createPersistedConsult() {
        Consult consult = createConsult();
        consult.setId(1L);
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

    private Doctor createPersistedDoctor() {
        User user = new User();
        user.setUsername("test-username");
        user.setPassword("test-pass");

        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setUser(user);
        doctor.getUser().getRoles().add(createDoctorRole());

        return doctor;
    }

    private Role createDoctorRole() {
        Role doctorRole = new Role();
        doctorRole.setName(ROLE_DOCTOR);
        return doctorRole;
    }

    private Role createPatientRole() {
        Role patientRole = new Role();
        patientRole.setName(ROLE_PATIENT);
        return patientRole;
    }

    private Role createAdminRole() {
        Role adminRole = new Role();
        adminRole.setName(ROLE_ADMIN);
        return adminRole;
    }

    private Patient createPersistedPatient() {
        Patient patient = createPatient();
        patient.setId(1L);
        patient.getUser().setPassword("enc_pass");
        patient.getUser().getRoles().add(createPatientRole());
        return patient;
    }

}