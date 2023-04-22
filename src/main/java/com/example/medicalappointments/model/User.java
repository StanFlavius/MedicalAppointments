package com.example.medicalappointments.model;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Getter
@Setter
@Builder
@Entity
@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "USER_ID")
    private Long id;

    @NotBlank(message = "Username must be provided!")
    private String username;

    @Pattern(regexp = "^(?=.*).{5,}",
            message = "Password must contain at least 5 characters!")
    @NotBlank(message = "Password must be provided!")
    private String password;

    @Email(message = "Email must be valid!")
    @NotBlank(message = "Email must be provided!")
    private String email;

    @Size(min = 2, message = "First name should have minimum 2 letters!")
    @Size(max = 30, message = "First name should have maximum 30 letters!")
    @NotBlank(message = "First name must be provided!")
    @Column(name = "FIRST_NAME")
    private String firstName;

    @Size(min = 2, message = "Last name should have minimum 2 letters!")
    @Size(max = 30, message = "Last name should have maximum 30 letters!")
    @NotBlank(message = "Last name must be provided!")
    @Column(name = "LAST_NAME")
    private String lastName;

    @ManyToOne
    @JoinColumn(name = "FK_ROLE_ID")
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Patient patient;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<Role> roles = new ArrayList<>();
        roles.add(getRole());

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(toList());
    }

    @Builder.Default
    @Transient
    private Boolean accountNotExpired = true;

    @Builder.Default
    @Transient
    private Boolean accountNotLocked = true;

    @Builder.Default
    @Transient
    private Boolean credentialsNotExpired = true;

    @Builder.Default
    @Transient
    private Boolean enabled = true;
}


