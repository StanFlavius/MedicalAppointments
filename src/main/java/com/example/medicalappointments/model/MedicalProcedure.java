package com.example.medicalappointments.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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

    @NotNull(message = "Procedure name must be provided!")
    @Size(min = 2, message = "Procedure name must have minimum 2 letters!")
    private String name;

    @NotNull(message = "Price must be provided!")
    @Min(value = 1, message = "Price must be positive!")
    private Integer price;

    @ManyToOne
    @JoinColumn(name = "FK_DEPARTMENT_ID")
    @JsonIgnore
    private Department department;

    @OneToMany(mappedBy = "medicalProcedure", cascade = CascadeType.ALL)
    private List<Consult> consults;
}
