package es.academia.campushub.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table

public class Actividad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable=false, unique=true, length=100)
    private String titulo;
    @Column(nullable=false, unique=true, length=1000)
    private String descripcion;
    @Column(nullable=false, length=100)
    private String tipo;
    private LocalDate fechaPrevista;
    @Column(nullable=false, length=100)
    private String maximosParticipantes;
    private Docente docente;
}
