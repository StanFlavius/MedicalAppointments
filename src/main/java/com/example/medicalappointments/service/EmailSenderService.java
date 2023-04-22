package com.example.medicalappointments.service;

import com.example.medicalappointments.model.Consult;
import com.example.medicalappointments.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import static com.example.medicalappointments.service.Helper.formatDate;

@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private final JavaMailSender emailSender;

    public void sendConsultAssignmentEmail(User user, Consult consult) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("homemanagement@gmail.com");
        message.setTo(user.getEmail());
        message.setSubject("You have been assigned a new consult");
        message.setText(getConsultAssignmentText(user, consult));
        emailSender.send(message);
    }

    private String getConsultAssignmentText(User user, Consult consult) {
        return String.format(
                        "Hello %s,\n\n" +
                        "You have been assigned a consult with the following details:\n" +
                        "Patient name: %s.\n" +
                        "Date: %s.\n\n" +
                        "Kind regards,\n" +
                        "Medical Appointments Team",
                user.getFirstName() + " " + user.getLastName(),
                consult.getPatient().getUser().getFirstName() + " " + consult.getPatient().getUser().getLastName(),
                formatDate(consult.getDate()));
    }
}
