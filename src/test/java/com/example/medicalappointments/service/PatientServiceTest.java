package com.example.medicalappointments.service;

import com.example.medicalappointments.exception.EntityNotFoundException;
import com.example.medicalappointments.exception.NotUniqueException;
import com.example.medicalappointments.model.*;
import com.example.medicalappointments.repository.PatientRepository;
import com.example.medicalappointments.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.example.medicalappointments.configuration.SecurityConfiguration.ROLE_DOCTOR;
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
    private DoctorService doctorService;

    @Mock
    private RoleService roleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PatientService patientService;

    @Test
    public void editPatient_success() {
        Role patientRole = createPatientRole();
        Patient patient = createPersistedPatient(patientRole);
        User user = patient.getUser();

        Patient patient2 = createPersistedPatient(patientRole);
        User user2 = patient2.getUser();
        user2.setFirstName("ABC");
        user2.setLastName("ABC");
        patient2.setUser(user2);

        when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
        when(patientRepository.save(any())).thenReturn(patient2);

        Patient resultedPatient = patientService.saveOrUpdateUser(patient, user);

        assertEquals(resultedPatient.getId(), patient2.getId());
        assertEquals(resultedPatient.getUser().getFirstName(), patient2.getUser().getFirstName());
        assertEquals(resultedPatient.getUser().getLastName(), patient2.getUser().getLastName());
    }

    @Test
    public void removePatient_success() {
        Role patientRole = createPatientRole();
        Patient patient = createPersistedPatient(patientRole);

        when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));

        Patient resultedPatient = patientService.findById(patient.getId());

        doNothing().when(patientRepository).deleteById(patient.getId());
        doNothing().when(userRepository).delete(patient.getUser());

        patientService.deletePatientById(patient.getId());

        assertEquals(resultedPatient.getId(), patient.getId());
        assertEquals(resultedPatient.getUser().getId(), patient.getUser().getId());
        assertTrue(resultedPatient.getUser().getRole().equals(patientRole));
        verify(patientRepository, times(1)).deleteById(patient.getId());
        verify(userRepository, times(1)).delete(patient.getUser());
        verify(patientRepository, times(2)).findById(patient.getId());
    }

    @Test
    public void getById_success() {
        Role patientRole = createPatientRole();
        Patient patient = createPersistedPatient(patientRole);

        when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));

        Patient resultedPatient = patientService.findById(patient.getId());

        assertEquals(resultedPatient.getId(), patient.getId());
        assertEquals(resultedPatient.getUser().getId(), patient.getUser().getId());
        assertTrue(resultedPatient.getUser().getRole().equals(patientRole));

        verify(patientRepository, times(1)).findById(patient.getId());
    }

    @Test
    public void getById_doctorNotFound_exception() {
        Long nonexistentDoctorId = 1L;

        when(patientRepository.findById(nonexistentDoctorId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> patientService.findById(nonexistentDoctorId));
    }


    @Test
    public void getAll_success() {
        Role patientRole = createPatientRole();
        Patient patient = createPersistedPatient(patientRole);

        when(patientRepository.findAll()).thenReturn(List.of(patient));

        List<Patient> resultedPatients = patientService.getAllPatients();

        assertEquals(1, resultedPatients.size());
        assertEquals(resultedPatients.get(0).getId(), patient.getId());
        assertEquals(resultedPatients.get(0).getUser().getId(), patient.getUser().getId());
        assertTrue(resultedPatients.get(0).getUser().getRole().equals(patientRole));

        verify(patientRepository, times(1)).findAll();
    }

    @Test
    public void getAllForDoctor_success() {
        Patient patient = createPatient();
        Patient persistedPatient = createPersistedPatient(createPatientRole());
        Doctor persistedDoctor = createPersistedDoctor();
        Consult persistedConsult = createPersistedConsult();
        persistedConsult.setPatient(persistedPatient);
        persistedConsult.setDoctor(persistedDoctor);

        when(userService.getCurrentUser()).thenReturn(persistedDoctor.getUser());
        when(doctorService.findByUserId(any())).thenReturn(persistedDoctor);

        when(patientRepository.findPatientsForDoctor(1L)).thenReturn(List.of(patient));

        List<Patient> resultedPatients = patientService.getAllPatients();

        assertEquals(1, resultedPatients.size());
        assertEquals(resultedPatients.get(0).getId(), patient.getId());
        assertEquals(resultedPatients.get(0).getUser().getId(), patient.getUser().getId());
        verify(patientRepository, times(1)).findPatientsForDoctor(1L);
    }

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
        assertTrue(resultedPatient.getUser().getRole().equals(patientRole));

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
        user.setId(1L);
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
        patient.getUser().setRole(role);
        return patient;
    }

    private Role createPatientRole() {
        Role patientRole = new Role();
        patientRole.setName(ROLE_PATIENT);
        return patientRole;
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

    private Role createDoctorRole() {
        Role doctorRole = new Role();
        doctorRole.setName(ROLE_DOCTOR);
        return doctorRole;
    }
    private Doctor createPersistedDoctor() {
        User user = new User();
        user.setUsername("test-username");
        user.setPassword("test-pass");

        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setUser(user);
        doctor.getUser().setRole(createDoctorRole());

        return doctor;
    }
}