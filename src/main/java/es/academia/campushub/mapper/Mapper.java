package es.academia.campushub.mapper;

import es.academia.campushub.controller.dto.ActividadResponse;
import es.academia.campushub.controller.dto.ContactoResponse;
import es.academia.campushub.controller.dto.DepartamentoResponse;
import es.academia.campushub.controller.dto.DocenteResponse;
import es.academia.campushub.controller.dto.EstudianteResponse;
import es.academia.campushub.controller.dto.GrupoResponse;
import es.academia.campushub.entity.Actividad;
import es.academia.campushub.entity.ContactoEstudiante;
import es.academia.campushub.entity.Departamento;
import es.academia.campushub.entity.Docente;
import es.academia.campushub.entity.Estudiante;
import es.academia.campushub.entity.Grupo;

public final class Mapper {
    private Mapper() {}

    public static DepartamentoResponse toResponse(Departamento d) {
        return new DepartamentoResponse(d.getId(), d.getNombre());
    }

    public static DocenteResponse toResponse(Docente d) {
        return new DocenteResponse(
                d.getId(), d.getNombre(), d.getApellidos(), d.getEmail(), d.getDepartamento().getId());
    }

    public static GrupoResponse toResponse(Grupo g) {
        return new GrupoResponse(g.getId(), g.getNombre(), g.getNivel(), g.getTutor().getId());
    }

    public static EstudianteResponse toResponse(Estudiante e) {
        return new EstudianteResponse(
                e.getId(),
                e.getNombre(),
                e.getApellidos(),
                e.getEmail(),
                e.getFechaNacimiento().toString(),
                e.getGrupo().getId());
    }

    public static ActividadResponse toResponse(Actividad a) {
        return new ActividadResponse(
                a.getId(),
                a.getTitulo(),
                a.getDescripcion(),
                a.getTipo(),
                a.getFechaPrevista().toString(),
                a.getMaxParticipantes(),
                a.getDocente().getId());
    }

    public static ContactoResponse toResponse(ContactoEstudiante c) {
        return new ContactoResponse(
                c.getId(),
                c.getTutorLegal(),
                c.getTelefono(),
                c.getEmail(),
                c.isAutorizado(),
                c.getFechaAutorizacion() == null ? null : c.getFechaAutorizacion().toString());
    }
}
