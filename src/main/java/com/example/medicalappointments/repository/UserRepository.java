package com.example.medicalappointments.repository;

import com.example.medicalappointments.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> getUserByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void deleteByUsername(String username);
}
