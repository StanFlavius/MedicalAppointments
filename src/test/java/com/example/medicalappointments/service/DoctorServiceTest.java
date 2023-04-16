package com.example.medicalappointments.service;//package com.example.project.service;

import com.example.medicalappointments.model.Doctor;
import com.example.medicalappointments.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("h2")
/**
 * Test for {@link DoctorService}
 */
public class DoctorServiceTest {

    @Autowired
    private DoctorService doctorService;

    @Test
    public void getAll() {
        List<Doctor> doctors = doctorService.getAllDoctors();

        assertEquals(3, doctors.size());
        assertThat(doctors.stream().map(Doctor::getUser).map(User::getLastName).collect(toList()),
                containsInAnyOrder("Sava", "Duncea", "Plamadeala"));
    }

}
