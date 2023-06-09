package com.example.medicalappointments.service;

import com.example.medicalappointments.exception.CustomException;
import com.example.medicalappointments.exception.EntityNotFoundException;
import com.example.medicalappointments.model.Doctor;
import com.example.medicalappointments.model.User;
import com.example.medicalappointments.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

import static com.example.medicalappointments.configuration.SecurityConfiguration.ROLE_DOCTOR;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public UserDetails getCurrentUserPrincipal() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        if (!(principal instanceof UserDetails)) {
            return null;
        }
        return (UserDetails) principal;
    }

    public boolean isLoggedIn() {
        return getCurrentUserPrincipal() != null;
    }

    public boolean hasRole(String role) {
        return getCurrentUserPrincipal().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch((auth) -> auth.equals(role));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getUserByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("Username: " + username));

        log.info("Loaded user: {}", user.getUsername());

        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(), user.getEnabled(), user.getAccountNotExpired(),
                user.getCredentialsNotExpired(), user.getAccountNotLocked(), getAuthorities(user.getAuthorities()));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Collection<? extends GrantedAuthority> authorities) {
        if (authorities == null || authorities.size() == 0) {
            return new HashSet<>();
        }
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    public User getCurrentUser() {
        UserDetails principal = getCurrentUserPrincipal();
        return userRepository.getUserByUsername(principal.getUsername()).orElseThrow(() ->
                EntityNotFoundException.builder().entityId(null).entityType("User").build());
    }

    public boolean isCurrentUserDoctor() {
        return hasRole(ROLE_DOCTOR);
    }

    public Doctor getDoctorForCurrentUser() {
        return userRepository.getDoctorForUsername(getCurrentUser().getUsername())
                .orElseThrow(() -> new CustomException("Current user is not a doctor!"));
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}

