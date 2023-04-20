package com.example.medicalappointments.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DOCTOR_ID")
    private Long id;

    private String skill;

    private String interests;

    private String memberIn;

    @OneToOne
    @JoinColumn(name = "FK_USER_ID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "FK_DEPARTMENT_ID")
    private Department department;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<Consult> consults;
}
