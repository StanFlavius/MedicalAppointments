package com.example.medicalappointments.service;

import com.example.medicalappointments.exception.EntityNotFoundException;
import com.example.medicalappointments.model.Doctor;
import com.example.medicalappointments.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Doctor findById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> EntityNotFoundException.builder()
                        .entityId(id)
                        .entityType("Doctor")
                        .build());
    }

    public void deleteDoctorById(Long id) {
        doctorRepository.deleteById(id);
    }
}
