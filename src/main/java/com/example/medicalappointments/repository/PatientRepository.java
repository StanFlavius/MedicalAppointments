package com.example.medicalappointments.repository;

import com.example.medicalappointments.model.Patient;
import com.example.medicalappointments.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    boolean existsByCnp(String cnp);
}
