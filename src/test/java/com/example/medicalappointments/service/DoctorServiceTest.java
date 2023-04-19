package com.example.medicalappointments.service;

import com.example.medicalappointments.exception.EntityNotFoundException;
import com.example.medicalappointments.model.Doctor;
import com.example.medicalappointments.model.Role;
import com.example.medicalappointments.model.User;
import com.example.medicalappointments.repository.DoctorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;
import java.util.Optional;

import static com.example.medicalappointments.configuration.SecurityConfiguration.ROLE_DOCTOR;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DoctorServiceTest {

    private static final String ENCODED_PASS = "encoded-pass";

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorService doctorService;

    @Test
    public void getAll_success() {
        Role doctorRole = createDoctorRole();
        Doctor doctor = createPersistedDoctor(doctorRole);

        when(doctorRepository.findAll()).thenReturn(List.of(doctor));

        List<Doctor> resultedDoctors = doctorService.getAllDoctors();

        assertEquals(1, resultedDoctors.size());
        assertEquals(resultedDoctors.get(0).getId(), doctor.getId());
        assertEquals(resultedDoctors.get(0).getUser().getId(), doctor.getUser().getId());
        assertTrue(resultedDoctors.get(0).getUser().getRoles().contains(doctorRole));

        verify(doctorRepository, times(1)).findAll();
    }

    @Test
    public void getById_success() {
        Role doctorRole = createDoctorRole();
        Doctor doctor = createPersistedDoctor(doctorRole);

        when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));

        Doctor resultedDoctor = doctorService.findById(doctor.getId());

        assertEquals(resultedDoctor.getId(), doctor.getId());
        assertEquals(resultedDoctor.getUser().getId(), doctor.getUser().getId());
        assertTrue(resultedDoctor.getUser().getRoles().contains(doctorRole));

        verify(doctorRepository, times(1)).findById(doctor.getId());
    }

    @Test
    public void getById_doctorNotFound_exception() {
        Role doctorRole = createDoctorRole();
        Doctor doctor = createPersistedDoctor(doctorRole);

        when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> doctorService.findById(doctor.getId()));
    }

    @Test
    public void removeDoctor_success() {
        Role doctorRole = createDoctorRole();
        Doctor doctor = createPersistedDoctor(doctorRole);

        doNothing().when(doctorRepository).deleteById(doctor.getId());

        doctorService.deleteDoctorById(doctor.getId());

        verify(doctorRepository, times(1)).deleteById(doctor.getId());
    }

    @Test
    public void removeDoctor_success_doctorNotFound_exception() {
        Role doctorRole = createDoctorRole();
        Doctor doctor = createPersistedDoctor(doctorRole);

        doThrow(EmptyResultDataAccessException.class).when(doctorRepository).deleteById(doctor.getId());

        assertThrows(EmptyResultDataAccessException.class, () -> doctorService.deleteDoctorById(doctor.getId()));
    }

    private Doctor createDoctor() {
        User user = new User();
        user.setUsername("test-username");
        user.setPassword("test-pass");
        user.setEmail("test@gmail.com");
        user.setFirstName("testfn");
        user.setLastName("testln");

        Doctor doctor = new Doctor();
        doctor.setInterests("testi");
        doctor.setMemberIn("testmi");
        doctor.setSkill("tests");
        doctor.setUser(user);

        return doctor;
    }

    private Doctor createPersistedDoctor(Role role) {
        Doctor patient = createDoctor();
        patient.setId(1L);
        patient.getUser().setPassword(ENCODED_PASS);
        patient.getUser().getRoles().add(role);
        return patient;
    }

    private Role createDoctorRole() {
        Role patientRole = new Role();
        patientRole.setName(ROLE_DOCTOR);
        return patientRole;
    }

}
