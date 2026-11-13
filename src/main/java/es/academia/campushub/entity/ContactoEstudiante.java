package es.academia.campushub.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table
public class ContactoEstudiante {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    @Column(nullable=false, length=100)
    private String nombreTutor;
    @Column(nullable=false, unique=true, length=100)
    private String telefono;
    @Column(nullable=false, unique=true, length=100)
    private String email;
    private boolean autorizacionActividades;
    private LocalDate fechaAutorizacion;
}
