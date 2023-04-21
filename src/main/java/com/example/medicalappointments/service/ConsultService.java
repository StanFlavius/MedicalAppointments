package com.example.medicalappointments.service;

import com.example.medicalappointments.exception.CustomException;
import com.example.medicalappointments.exception.EntityNotFoundException;
import com.example.medicalappointments.model.Consult;
import com.example.medicalappointments.model.Patient;
import com.example.medicalappointments.model.Role;
import com.example.medicalappointments.repository.ConsultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.medicalappointments.configuration.SecurityConfiguration.ROLE_PATIENT;

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

    public List<Consult> getAllConsults() {
        Set<String> roles = userService.getCurrentUser().getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        if (roles.contains(ROLE_PATIENT)) {
            Patient patient = patientService.findByUserId(userService.getCurrentUser().getId());
            return consultRepository.findAllByPatient_Id(patient.getId());
        }
        return consultRepository.findAll();
    }

    public Consult getConsultById(Long id) {
        return consultRepository.findById(id)
                .orElseThrow(() -> EntityNotFoundException.builder()
                        .entityId(id)
                        .entityType("Consult")
                        .build());
    }

    public void deleteConsultById(Long id) {
        Consult consult = getConsultById(id);
        if (consult.getDate().before(new Date())) {
            throw new CustomException("You cannot delete a past consult!");
        }
        consultRepository.delete(consult);
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
