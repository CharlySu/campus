package es.academia.campushub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(
        name = "estudiante",
        uniqueConstraints = @UniqueConstraint(name = "uk_estudiante_email", columnNames = "email"),
        indexes = @Index(name = "ix_estudiante_grupo", columnList = "grupo_id"),
        check =
        @CheckConstraint(
                name = "ck_estudiante_nacimiento",
                constraint = "fecha_nacimiento < CURRENT_DATE"))
@Getter
@Setter
public class Estudiante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 80, message = "El nombre no puede superar los 80 caracteres")
    private String nombre;

    @Column(nullable = false, length = 120)
    @NotBlank(message = "Los apellidos no pueden estar vacíos")
    @Size(max = 120, message = "Los apellidos no pueden superar los 120 caracteres")
    private String apellidos;

    @Column(nullable = false, length = 254, unique = true)
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Debe proporcionar un formato de correo electrónico válido")
    @Size(max = 254, message = "El email no puede superar los 254 caracteres")
    private String email;

    @Column(name = "fecha_nacimiento", nullable = false)
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser anterior a la actual")
    private LocalDate fechaNacimiento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grupo_id", nullable = false)
    private Grupo grupo;

    public Estudiante() {}
}