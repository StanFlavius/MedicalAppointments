package com.example.medicalappointments.repository;

import com.example.medicalappointments.model.Department;
import com.example.medicalappointments.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findByDepartment(Department department);

    Optional<Doctor> findByUser_Id(Long userId);
}
