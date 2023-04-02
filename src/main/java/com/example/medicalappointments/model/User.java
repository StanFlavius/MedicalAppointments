package com.example.medicalappointments.model;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Set;

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

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID")
    )
    private Set<Role> roles;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles().stream()
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


