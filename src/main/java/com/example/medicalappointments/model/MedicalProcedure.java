package com.example.medicalappointments.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Table(name = "Medical_procedure")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalProcedure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROECEDURE_ID")
    private Long id;

    private String name;

    @NotNull(message = "Price must be provided!")
    @Min(value = 1, message = "Price must be positive!")
    private Integer price;

    @ManyToOne
    @JoinColumn(name = "FK_DEPARTMENT_ID")
    private Department department;

    @OneToMany(mappedBy = "medicalProcedure", cascade = CascadeType.ALL)
    private List<Consult> consults;
}
