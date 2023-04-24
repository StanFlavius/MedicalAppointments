package com.example.medicalappointments.repository;

import com.example.medicalappointments.model.Doctor;
import com.example.medicalappointments.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select d from Doctor d join d.user u where u.username = :username")
    Optional<Doctor> getDoctorForUsername(String username);

    Optional<User> getUserByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void deleteByUsername(String username);
}
