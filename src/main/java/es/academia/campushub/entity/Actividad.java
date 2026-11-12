package es.academia.campushub.entity;

import java.time.LocalDate;

public class Actividad {
    private long id;
    private String titulo;
    private String descripcion;
    private String tipo;
    private LocalDate fechaPrevista;
    private String maximosParticipantes;
    private Docente docente;
}
