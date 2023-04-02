package com.example.medicalappointments.service;

import com.example.medicalappointments.exception.EntityNotFoundException;
import com.example.medicalappointments.model.User;
import com.example.medicalappointments.model.security.MyUser;
import com.example.medicalappointments.model.security.Role;
import com.example.medicalappointments.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.example.medicalappointments.configuration.SecurityConfiguration.*;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return new MyUser(user);
    }

    public List<User> getAllDoctors() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRoles().stream().map(Role::getName).collect(toList()).contains(ROLE_DOCTOR))
                .collect(toList());
    }

    public List<User> getAllPatients() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRoles().stream().map(Role::getName).collect(toList()).contains(ROLE_PATIENT))
                .collect(toList());
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> EntityNotFoundException.builder().entityId(id).entityType("User").build());
    }

    public boolean isDoctor() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            List<String> roles = getRoles(principal);
            return roles.contains(ROLE_DOCTOR);
        }
        return false;
    }

    public boolean isPatient() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            List<String> roles = getRoles(principal);
            return roles.contains(ROLE_PATIENT);
        }
        return false;
    }

    public boolean isAdmin() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            List<String> roles = getRoles(principal);
            return roles.contains(ROLE_ADMIN);
        }
        return false;
    }

    public boolean checkIfCurrentUserIsSameDoctor(Long doctorId) {
        return Objects.equals(doctorId, getCurrentUser().getId());
    }

    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            Optional<User> user = userRepository.getUserByUsername(username);
            if (user.isEmpty()) {
                throw EntityNotFoundException.builder().entityId(null).entityType("User").build();
            }
            return user.get();
        }
        throw EntityNotFoundException.builder().entityId(null).entityType("User").build();
    }

    public static boolean isLoggedIn() {
        try {
            var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return principal instanceof UserDetails;
        } catch (Exception e) {
            return false;
        }
    }

    public void deleteById(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    private List<String> getRoles(Object principal) {
        var authorities = (Collection<SimpleGrantedAuthority>) ((UserDetails) principal).getAuthorities();
        return authorities.stream()
                .map(SimpleGrantedAuthority::getAuthority)
                .collect(toList());
    }
}
