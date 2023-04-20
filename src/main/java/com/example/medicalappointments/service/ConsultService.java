package com.example.medicalappointments.service;

import com.example.medicalappointments.exception.CustomException;
import com.example.medicalappointments.exception.EntityNotFoundException;
import com.example.medicalappointments.model.Consult;
import com.example.medicalappointments.model.Patient;
import com.example.medicalappointments.repository.ConsultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Calendar;
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
        validateConsult(consult);
        Patient patient = patientService.findByUserId(userService.getCurrentUser().getId());
        consult.setPatient(patient);

        return consultRepository.save(consult);
    }

    private void validateConsult(Consult consult) {
        Date date = consult.getDate();
        Date start = setHour(date, 8);
        Date end = setHour(date, 18);

        if (date.before(start) || date.after(end)) {
            throw new CustomException("The time must be in the working hours!");
        }
        if (!consultRepository.findConsultsInTimeRange(addHours(date, -1), addHours(date, 1), consult.getDoctor()).isEmpty()) {
            throw new CustomException("There is already an appointment at this time, please try something else.");
        }
    }

    private Date addHours(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

    private Date setHour(Date date, int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        return calendar.getTime();
    }
}
