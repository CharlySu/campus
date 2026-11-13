package es.academia.campushub.entity;

import jakarta.persistence.*;

@Entity
@Table(name="departamento")

public class Departamento {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    @Column(nullable=false, unique=true, length=100)
    private String nombre;
}
