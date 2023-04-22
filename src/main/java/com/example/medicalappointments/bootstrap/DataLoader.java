package com.example.medicalappointments.bootstrap;

import com.example.medicalappointments.model.*;
import com.example.medicalappointments.model.Role;
import com.example.medicalappointments.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.Date;

import static com.example.medicalappointments.configuration.SecurityConfiguration.*;

@Component
@RequiredArgsConstructor
@Profile({ "dataLoader", "h2" })
public class DataLoader implements CommandLineRunner {

    private final EntityManager entityManager;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DepartmentRepository departmentRepository;
    private final MedicationRepository medicationRepository;
    private final ConsultRepository consultRepository;
    private final PasswordEncoder passwordEncoder;
    private final MedicalProcedureRepository medicalProcedureRepository;

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

            Department department1 = Department.builder()
                    .name("Cardiologie")
                    .build();

            Department department2 = Department.builder()
                    .name("Neurologie")
                    .build();

            departmentRepository.save(department1);
            departmentRepository.save(department2);

            MedicalProcedure medicalProcedure1 = MedicalProcedure.builder()
                    .name("EKG")
                    .price(100)
                    .department(department1)
                    .build();

            MedicalProcedure medicalProcedure2 = MedicalProcedure.builder()
                    .name("Holter")
                    .price(150)
                    .department(department1)
                    .build();

            MedicalProcedure medicalProcedure3 = MedicalProcedure.builder()
                    .name("Bypasser")
                    .price(500)
                    .department(department1)
                    .build();

            MedicalProcedure medicalProcedure4 = MedicalProcedure.builder()
                    .name("EMG")
                    .price(120)
                    .department(department2)
                    .build();

            MedicalProcedure medicalProcedure5 = MedicalProcedure.builder()
                    .name("Tomografie computerizata")
                    .price(180)
                    .department(department2)
                    .build();

            medicalProcedureRepository.save(medicalProcedure1);
            medicalProcedureRepository.save(medicalProcedure2);
            medicalProcedureRepository.save(medicalProcedure3);
            medicalProcedureRepository.save(medicalProcedure4);
            medicalProcedureRepository.save(medicalProcedure5);

            User userAdmin = User.builder()
                    .username("admin_1")
                    .password(passwordEncoder.encode("123456"))
                    .email("admin_1@email.com")
                    .firstName("Admin")
                    .lastName("Admin")
                    .role(adminRole)
                    .build();

            User userDoctor1 = User.builder()
                    .username("doctor_1")
                    .password(passwordEncoder.encode("123456"))
                    .email("doctor_1@email.com")
                    .firstName("Alin")
                    .lastName("Sava")
                    .role(doctorRole)
                    .build();

            User userDoctor2 = User.builder()
                    .username("doctor_2")
                    .password(passwordEncoder.encode("123456"))
                    .email("doctor_2@email.com")
                    .firstName("Vlad")
                    .lastName("Duncea")
                    .role(doctorRole)
                    .build();

            User userDoctor3 = User.builder()
                    .username("doctor_3")
                    .password(passwordEncoder.encode("123456"))
                    .email("doctor_3@email.com")
                    .firstName("Cristian")
                    .lastName("Plamadeala")
                    .role(doctorRole)
                    .build();

            User userPacient1 = User.builder()
                    .username("pacient_1")
                    .password(passwordEncoder.encode("123456"))
                    .email("pacient_1@email.com")
                    .firstName("Marius")
                    .lastName("Iordache")
                    .role(pacientRole)
                    .build();

            User userPacient2 = User.builder()
                    .username("pacient_2")
                    .password(passwordEncoder.encode("123456"))
                    .email("pacient_2@email.com")
                    .firstName("Cristina")
                    .lastName("Stefanescu")
                    .role(pacientRole)
                    .build();

            userRepository.save(userAdmin);

            userRepository.save(userDoctor1);
            userRepository.save(userDoctor2);
            userRepository.save(userDoctor3);

            userRepository.save(userPacient1);
            userRepository.save(userPacient2);

            Doctor doctor1 = Doctor.builder()
                    .user(userDoctor1)
                    .department(department1)
                    .interests("Chirurgie toracica")
                    .skill("EKG")
                    .memberIn("Colegiul Medicilor")
                    .build();

            Doctor doctor2 = Doctor.builder()
                    .user(userDoctor2)
                    .department(department1)
                    .interests("Chirurgie minim-invaziva")
                    .skill("Da Vinci")
                    .memberIn("Colegiul Medicilor si ANOSR")
                    .build();

            Doctor doctor3 = Doctor.builder()
                    .user(userDoctor3)
                    .department(department1)
                    .interests("-")
                    .skill("Skill1, skill2")
                    .memberIn("Fara")
                    .build();

            doctorRepository.save(doctor1);
            doctorRepository.save(doctor2);
            doctorRepository.save(doctor3);

            Patient patient1 = Patient.builder()
                    .cnp("1111111111111")
                    .user(userPacient1)
                    .build();

            Patient patient2 = Patient.builder()
                    .cnp("2222222222222")
                    .user(userPacient2)
                    .build();

            patientRepository.save(patient1);
            patientRepository.save(patient2);

            Medication medication1 = Medication.builder()
                    .name("Paracetamol")
                    .quantity(300)
                    .build();

            Medication medication2 = Medication.builder()
                    .name("Paracetamol")
                    .quantity(600)
                    .build();

            Medication medication3 = Medication.builder()
                    .name("Ibuprofen")
                    .quantity(500)
                    .build();

            Medication medication4 = Medication.builder()
                    .name("Controloc")
                    .quantity(1000)
                    .build();

            medicationRepository.save(medication1);
            medicationRepository.save(medication2);
            medicationRepository.save(medication3);
            medicationRepository.save(medication4);

            Consult consult1 = Consult.builder()
                    .patient(patient1)
                    .doctor(doctor1)
                    .date(new Date())
                    .diagnose("Streptococ agravat")
                    .symptoms("Durere in gat")
                    .build();

            Consult consult2 = Consult.builder()
                    .patient(patient2)
                    .doctor(doctor3)
                    .date(new Date())
                    .diagnose("Reumatism sever")
                    .symptoms("Dureri articulare")
                    .build();

            consultRepository.save(consult1);
            consultRepository.save(consult2);
        }
    }
}
