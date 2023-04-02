package com.example.medicalappointments.service;

import com.example.medicalappointments.exception.EntityNotFoundException;
import com.example.medicalappointments.exception.NotUniqueException;
import com.example.medicalappointments.model.Patient;
import com.example.medicalappointments.model.Role;
import com.example.medicalappointments.model.User;
import com.example.medicalappointments.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.example.medicalappointments.configuration.SecurityConfiguration.ROLE_PATIENT;
import static com.example.medicalappointments.exception.NotUniqueException.ConflictingField.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    private static final String ENCODED_PASS = "encoded-pass";

    @Mock
    private UserService userService;

    @Mock
    private RoleService roleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    @Test
    void createPatient_success() {
        Role patientRole = createPatientRole();
        Patient patient = createPatient();
        Patient persistedPatient = createPersistedPatient(patientRole);

        when(roleService.getRoleByName(ROLE_PATIENT)).thenReturn(patientRole);
        when(passwordEncoder.encode(patient.getUser().getPassword())).thenReturn(ENCODED_PASS);
        when(userService.existsByUsername(patient.getUser().getUsername())).thenReturn(false);
        when(userService.existsByEmail(patient.getUser().getEmail())).thenReturn(false);
        when(patientRepository.existsByCnp(patient.getCnp())).thenReturn(false);
        when(patientRepository.save(patient)).thenReturn(persistedPatient);

        Patient resultedPatient = patientService.createPatient(patient);

        assertNotNull(resultedPatient);
        assertEquals(persistedPatient.getId(), resultedPatient.getId());
        assertEquals(persistedPatient.getUser().getId(), resultedPatient.getUser().getId());
        assertEquals(ENCODED_PASS, resultedPatient.getUser().getPassword());
        assertTrue(resultedPatient.getUser().getRoles().contains(patientRole));

        verify(patientRepository, times(1)).existsByCnp(patient.getCnp());
        verify(patientRepository, times(1)).save(patient);
    }

    @Test
    void createPatient_patientRoleNotFound_exception() {
        Patient patient = createPatient();

        doThrow(new EntityNotFoundException("Role", null)).when(roleService).getRoleByName(ROLE_PATIENT);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> patientService.createPatient(patient));

        assertNull(exception.getEntityId());
        assertEquals("Role", exception.getEntityType());
    }

    @Test
    void createPatient_notUniqueUsername_exception() {
        Role patientRole = createPatientRole();
        Patient patient = createPatient();

        when(roleService.getRoleByName(ROLE_PATIENT)).thenReturn(patientRole);
        when(passwordEncoder.encode(patient.getUser().getPassword())).thenReturn(ENCODED_PASS);
        when(userService.existsByUsername(patient.getUser().getUsername())).thenReturn(true);

        NotUniqueException exception = assertThrows(NotUniqueException.class, () -> patientService.createPatient(patient));

        assertEquals(USERNAME, exception.getConflictingField());
        assertEquals(String.format("Username %s already exists!", patient.getUser().getUsername()), exception.getMessage());
    }

    @Test
    void createPatient_notUniqueEmail_exception() {
        Role patientRole = createPatientRole();
        Patient patient = createPatient();

        when(roleService.getRoleByName(ROLE_PATIENT)).thenReturn(patientRole);
        when(passwordEncoder.encode(patient.getUser().getPassword())).thenReturn(ENCODED_PASS);
        when(userService.existsByUsername(patient.getUser().getUsername())).thenReturn(false);
        when(userService.existsByEmail(patient.getUser().getEmail())).thenReturn(true);

        NotUniqueException exception = assertThrows(NotUniqueException.class, () -> patientService.createPatient(patient));

        assertEquals(EMAIL, exception.getConflictingField());
        assertEquals(String.format("Email %s already exists!", patient.getUser().getEmail()), exception.getMessage());
    }

    @Test
    void createPatient_notUniqueCnp_exception() {
        Role patientRole = createPatientRole();
        Patient patient = createPatient();

        when(roleService.getRoleByName(ROLE_PATIENT)).thenReturn(patientRole);
        when(passwordEncoder.encode(patient.getUser().getPassword())).thenReturn(ENCODED_PASS);
        when(userService.existsByUsername(patient.getUser().getUsername())).thenReturn(false);
        when(userService.existsByEmail(patient.getUser().getEmail())).thenReturn(false);
        when(patientRepository.existsByCnp(patient.getCnp())).thenReturn(true);

        NotUniqueException exception = assertThrows(NotUniqueException.class, () -> patientService.createPatient(patient));

        assertEquals(CNP, exception.getConflictingField());
        assertEquals(String.format("Cnp %s already exists!", patient.getCnp()), exception.getMessage());
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

    private Patient createPersistedPatient(Role role) {
        Patient patient = createPatient();
        patient.setId(1L);
        patient.getUser().setPassword(ENCODED_PASS);
        patient.getUser().getRoles().add(role);
        return patient;
    }

    private Role createPatientRole() {
        Role patientRole = new Role();
        patientRole.setName(ROLE_PATIENT);
        return patientRole;
    }

}