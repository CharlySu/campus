package es.academia.campushub.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "grupo")

public class Grupo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, length = 100)
    private String Nombre;
    @Column(nullable=false, length=100)
    private String Nivel;
    @Column(nullable=false, length=100)
    private Docente tutor;
}
