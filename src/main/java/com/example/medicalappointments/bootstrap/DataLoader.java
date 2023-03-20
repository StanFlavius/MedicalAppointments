package com.example.medicalappointments.bootstrap;

import com.example.medicalappointments.model.User;
import com.example.medicalappointments.model.security.Role;
import com.example.medicalappointments.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;

import static com.example.medicalappointments.configuration.SecurityConfiguration.*;

@Component
@RequiredArgsConstructor
@Profile("dataLoader")
public class DataLoader implements CommandLineRunner {

    private final EntityManager entityManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        loadUserData();
    }

    @Transactional
    void loadUserData() {
        if (userRepository.count() == 0) {

            Role adminRole = Role.builder()
                    .name(ROLE_ADMIN)
                    .build();

            Role doctorRole = Role.builder()
                    .name(ROLE_DOCTOR)
                    .build();

            Role pacientRole = Role.builder()
                    .name(ROLE_PATIENT)
                    .build();

            entityManager.persist(adminRole);
            entityManager.persist(doctorRole);
            entityManager.persist(pacientRole);

            User userAdmin = User.builder()
                    .username("admin_1")
                    .password(passwordEncoder.encode("123456"))
                    .email("admin_1@email.com")
                    .firstName("Admin")
                    .lastName("Admin")
                    .roles(Collections.singleton(adminRole))
                    .build();

            User userDoctor1 = User.builder()
                    .username("doctor_1")
                    .password(passwordEncoder.encode("123456"))
                    .email("doctor_1@email.com")
                    .firstName("Doctor")
                    .lastName("Unu")
                    .roles(Collections.singleton(doctorRole))
                    .build();

            User userDoctor2 = User.builder()
                    .username("doctor_2")
                    .password(passwordEncoder.encode("123456"))
                    .email("doctor_2@email.com")
                    .firstName("Doctor")
                    .lastName("Doi")
                    .roles(Collections.singleton(doctorRole))
                    .build();

            User userDoctor3 = User.builder()
                    .username("doctor_3")
                    .password(passwordEncoder.encode("123456"))
                    .email("doctor_3@email.com")
                    .firstName("Doctor")
                    .lastName("Trei")
                    .roles(Collections.singleton(doctorRole))
                    .build();

            User userPacient1 = User.builder()
                    .username("pacient_1")
                    .password(passwordEncoder.encode("123456"))
                    .email("pacient_1@email.com")
                    .firstName("Pacient")
                    .lastName("Unu")
                    .roles(Collections.singleton(pacientRole))
                    .build();

            User userPacient2 = User.builder()
                    .username("pacient_2")
                    .password(passwordEncoder.encode("123456"))
                    .email("pacient_2@email.com")
                    .firstName("Pacient")
                    .lastName("Doi")
                    .roles(Collections.singleton(pacientRole))
                    .build();

            userRepository.save(userAdmin);

            userRepository.save(userDoctor1);
            userRepository.save(userDoctor2);
            userRepository.save(userDoctor3);

            userRepository.save(userPacient1);
            userRepository.save(userPacient2);
        }
    }
}
