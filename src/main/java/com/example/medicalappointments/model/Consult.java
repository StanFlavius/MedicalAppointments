package com.example.medicalappointments.model;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Consult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONSULT_ID")
    private Long id;

    @NotNull(message = "Date must be provided!")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "yyyy-MM-dd'T'HH:mm")
    private Date date = new Date();

    @Length(min = 10, message = "Please enter at least 10 characters!")
    private String diagnose;

    @Length(min = 10, message = "Please enter at least 10 characters!")
    private String symptoms;

    @Length(min = 5, message = "Please enter at least 5 characters!")
    private String comment;

    @ManyToOne
    @JoinColumn(name = "FK_DOCTOR_ID")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "FK_PATIENT_ID")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "FK_PROCEDURE_ID")
    private MedicalProcedure medicalProcedure;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "Prescription",
            joinColumns = @JoinColumn(name = "CONSULT_ID", referencedColumnName = "CONSULT_ID"),
            inverseJoinColumns = @JoinColumn(name = "MEDICATION_ID", referencedColumnName = "MEDICATION_ID"))
    private List<Medication> medications;
}
