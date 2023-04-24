package com.example.medicalappointments.repository;

import com.example.medicalappointments.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    boolean existsByCnp(String cnp);

    Optional<Patient> findByUser_Id(Long userId);
}
