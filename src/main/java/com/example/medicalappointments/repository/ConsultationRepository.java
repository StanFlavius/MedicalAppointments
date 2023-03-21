package com.example.medicalappointments.repository;

import com.example.medicalappointments.model.Consult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsultationRepository extends JpaRepository<Consult, Long> {
}
