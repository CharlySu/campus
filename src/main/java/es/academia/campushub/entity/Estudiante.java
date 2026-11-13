package es.academia.campushub.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table

public class Estudiante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, length = 100)
    private String nombre;
    @Column(nullable=false, length=100)
    private String apellidos;
    @Column(nullable=false, unique=true, length=100)
    private String email;
    private LocalDate fechaNacimiento;
    private Grupo grupo;

}
