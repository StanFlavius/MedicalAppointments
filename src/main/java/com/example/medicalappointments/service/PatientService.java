package com.example.medicalappointments.service;

import com.example.medicalappointments.exception.EntityNotFoundException;
import com.example.medicalappointments.exception.NotUniqueException;
import com.example.medicalappointments.model.Consult;
import com.example.medicalappointments.model.Doctor;
import com.example.medicalappointments.model.Patient;
import com.example.medicalappointments.model.User;
import com.example.medicalappointments.repository.ConsultRepository;
import com.example.medicalappointments.repository.PatientRepository;
import com.example.medicalappointments.repository.RoleRepository;
import com.example.medicalappointments.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static com.example.medicalappointments.configuration.SecurityConfiguration.ROLE_PATIENT;
import static com.example.medicalappointments.exception.NotUniqueException.ConflictingField.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final PatientRepository patientRepository;
    private final ConsultRepository consultRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public void deletePatientById(Long id){
        Patient patient = patientRepository.findById(id).get();

        patientRepository.deleteById(id);
        userRepository.delete(patient.getUser());
    }

    public Patient findById(Long id){
        return patientRepository.findById(id)
                .orElseThrow(() -> EntityNotFoundException.builder()
                        .entityId(id)
                        .entityType("Patient")
                        .build());
    }

    public Patient createPatient(Patient patient) {
        User user = patient.getUser();
        user.setRole(roleService.getRoleByName(ROLE_PATIENT));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (userService.existsByUsername(user.getUsername())) {
            throw new NotUniqueException(USERNAME, String.format("Username %s already exists!", user.getUsername()));
        }
        if (userService.existsByEmail(patient.getUser().getEmail())) {
            throw new NotUniqueException(EMAIL, String.format("Email %s already exists!", user.getEmail()));
        }
        if (patientRepository.existsByCnp(patient.getCnp())) {
            throw new NotUniqueException(CNP, String.format("Cnp %s already exists!", patient.getCnp()));
        }

        return patientRepository.save(patient);
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient findByUserId(Long userId) {
        return patientRepository.findByUser_Id(userId)
                .orElseThrow(() -> EntityNotFoundException.builder()
                        .entityType("Patient")
                        .build());
    }

    public Patient saveOrUpdateUser(Patient patient, User user) {
        Patient patientInDB = findById(patient.getId());
        User userInDB = patientInDB.getUser();

        userInDB.setFirstName(user.getFirstName());
        userInDB.setLastName(user.getLastName());

        patient.setCnp(patientInDB.getCnp());
        userRepository.save(userInDB);

        patient.setUser(userInDB);
        return patientRepository.save(patient);
    }
}
