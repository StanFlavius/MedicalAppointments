package com.example.medicalappointments.service;

import com.example.medicalappointments.exception.NotUniqueException;
import com.example.medicalappointments.model.Patient;
import com.example.medicalappointments.model.User;
import com.example.medicalappointments.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static com.example.medicalappointments.configuration.SecurityConfiguration.ROLE_PATIENT;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final UserService userService;
    private final RoleService roleService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final PatientRepository patientRepository;

    public Patient createPatient(Patient patient) {
        User user = patient.getUser();
        user.setRoles(Collections.singleton(roleService.getRoleByName(ROLE_PATIENT)));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        if (userService.existsByUsername(user.getUsername())) {
            throw new NotUniqueException(String.format("Username %s already exists!", user.getUsername()));
        }
        if (userService.existsByEmail(patient.getUser().getEmail())) {
            throw new NotUniqueException(String.format("Email %s already exists!", user.getEmail()));
        }
        if (patientRepository.existsByCnp(patient.getCnp())) {
            throw new NotUniqueException(String.format("Cnp %s already exists!", patient.getCnp()));
        }

        return patientRepository.save(patient);
    }
}
