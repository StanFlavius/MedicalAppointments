package com.example.medicalappointments.service;

import com.example.medicalappointments.model.Consult;
import com.example.medicalappointments.model.Patient;
import com.example.medicalappointments.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailSenderServiceTest {

    @Mock
    private JavaMailSender emailSender;

    @InjectMocks
    private EmailSenderService emailSenderService;

    @Test
    void sendConsultAssignmentEmail_success() {
        User userDoctor = createUser();
        Consult consult = createConsult();

        emailSenderService.sendConsultAssignmentEmail(userDoctor, consult);

        verify(emailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    private Consult createConsult() {
        return Consult.builder()
                .patient(createPatient())
                .date(new Date())
                .build();
    }

    private Patient createPatient() {
        return Patient.builder()
                .user(createUser())
                .build();
    }

    private User createUser() {
        return User.builder()
                .email("test@test.ro")
                .firstName("Testfn")
                .lastName("Testln")
                .build();
    }

}