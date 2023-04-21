package com.example.medicalappointments.service;

import com.example.medicalappointments.exception.EntityNotFoundException;
import com.example.medicalappointments.model.Department;
import com.example.medicalappointments.model.Doctor;
import com.example.medicalappointments.model.Role;
import com.example.medicalappointments.model.User;
import com.example.medicalappointments.repository.DepartmentRepository;
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

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DoctorService doctorService;

    @Test
    public void getDoctorsByDepartment() {
        Role doctorRole = createDoctorRole();
        Department department = createDepartment();
        Doctor doctor = createPersistedDoctor2(doctorRole, department);

        when(departmentRepository.getById(10L)).thenReturn(department);
        when(doctorRepository.findByDepartment(department)).thenReturn(List.of(doctor));

        List<Doctor> resultedDoctors = doctorService.getDoctorsByDepartment(Long.valueOf("10"));

        assertEquals(1, resultedDoctors.size());
        assertEquals(resultedDoctors.get(0).getId(), doctor.getId());
        assertEquals(resultedDoctors.get(0).getUser().getId(), doctor.getUser().getId());
        assertTrue(resultedDoctors.get(0).getUser().getRoles().contains(doctorRole));

        verify(doctorRepository, times(1)).findByDepartment(department);
        verify(departmentRepository, times(1)).getById(10L);
    }

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
        Long nonexistentDoctorId = 1L;
        
        when(doctorRepository.findById(nonexistentDoctorId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> doctorService.findById(nonexistentDoctorId));
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
    public void removeDoctor_doctorNotFound_exception() {
        Role doctorRole = createDoctorRole();
        Doctor doctor = createPersistedDoctor(doctorRole);

        doThrow(EmptyResultDataAccessException.class).when(doctorRepository).deleteById(doctor.getId());

        assertThrows(EmptyResultDataAccessException.class, () -> doctorService.deleteDoctorById(doctor.getId()));
    }

    private Department createDepartment(){
        Department department = new Department();
        department.setId(10L);
        department.setName("Pediatrie");

        return department;
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

    private Doctor createPersistedDoctor2(Role role, Department department) {
        Doctor doctor = createDoctor();
        doctor.setDepartment(department);
        doctor.setId(1L);
        doctor.getUser().setPassword(ENCODED_PASS);
        doctor.getUser().getRoles().add(role);
        return doctor;
    }

    private Doctor createPersistedDoctor(Role role) {
        Doctor doctor = createDoctor();
        doctor.setId(1L);
        doctor.getUser().setPassword(ENCODED_PASS);
        doctor.getUser().getRoles().add(role);
        return doctor;
    }

    private Role createDoctorRole() {
        Role doctorRole = new Role();
        doctorRole.setName(ROLE_DOCTOR);
        return doctorRole;
    }

}
