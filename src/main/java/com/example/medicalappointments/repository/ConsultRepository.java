package com.example.medicalappointments.repository;

import com.example.medicalappointments.model.Consult;
import com.example.medicalappointments.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ConsultRepository extends JpaRepository<Consult, Long> {

    @Query(value = "SELECT c FROM Consult c where c.doctor = :doctor AND c.date BETWEEN :dateStart AND :dateEnd")
    List<Consult> findConsultsInTimeRange(@Param("dateStart") Date dateStart, @Param("dateEnd") Date dateEnd, @Param("doctor") Doctor doctor);
}
