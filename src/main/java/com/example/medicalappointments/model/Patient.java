package com.example.medicalappointments.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

import static com.example.medicalappointments.model.Regex.CNP_REGEX;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PATIENT_ID")
    private Long id;

    @Pattern(regexp = CNP_REGEX, message = "Invalid CNP!")
    @NotBlank(message = "CNP must be provided!")
    private String cnp;

    @OneToOne
    @JoinColumn(name = "FK_USER_ID")
    private User user;

    @OneToMany(mappedBy = "medicalProcedure", cascade = CascadeType.ALL)
    private List<Consult> consults;
}
