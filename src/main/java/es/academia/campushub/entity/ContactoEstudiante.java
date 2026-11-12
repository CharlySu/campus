package es.academia.campushub.entity;

import java.time.LocalDate;

public class ContactoEstudiante extends Estudiante {
    private long id;
    private String nombreTutor;
    private String telefono;
    private String email;
    private boolean autorizacionActividades;
    private LocalDate fechaAutorizacion;
}
