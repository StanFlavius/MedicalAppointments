package com.example.medicalappointments.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEDICATION_ID")
    private Long id;

    @NotBlank(message = "Medication name must be provided!")
    private String name;

    @NotNull(message = "Quantity must be provided!")
    @Min(value = 1, message = "Quantity must be positive!")
    private Integer quantity;

    @ManyToMany
    @JoinTable(name = "Prescription",
            joinColumns = @JoinColumn(name = "MEDICATION_ID", referencedColumnName = "MEDICATION_ID"),
            inverseJoinColumns = @JoinColumn(name = "CONSULT_ID", referencedColumnName = "CONSULT_ID"))
    private List<Consult> consults;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Medication that = (Medication) o;
        return id.equals(that.id) && name.equals(that.name) && quantity.equals(that.quantity) && consults.equals(that.consults);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, quantity, consults);
    }
}
