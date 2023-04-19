package com.example.medicalappointments.service;

import com.example.medicalappointments.exception.CustomException;
import com.example.medicalappointments.model.Consult;
import com.example.medicalappointments.model.Patient;
import com.example.medicalappointments.repository.ConsultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ConsultService {

    private final ConsultRepository consultRepository;
    private final UserService userService;
    private final PatientService patientService;

    public Consult saveConsult(Consult consult) {
        if (consult.getDate().before(new Date())) {
            throw new CustomException("Date must be in the future!");
        }
        Patient patient = patientService.findByUserId(userService.getCurrentUser().getId());
        consult.setPatient(patient);

        return consultRepository.save(consult);
    }
}
