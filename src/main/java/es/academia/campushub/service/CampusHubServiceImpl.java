package es.academia.campushub.service;

import es.academia.campushub.controller.dto.*;
import es.academia.campushub.entity.*;
import es.academia.campushub.mapper.Mapper;
import es.academia.campushub.repository.*;
import es.academia.campushub.util.Validaciones;
import es.academia.campushub.util.exception.BusinessConflictException;
import es.academia.campushub.util.exception.InvalidInputException;
import es.academia.campushub.util.exception.PersistenciaPendienteException;

import java.time.LocalDate;
import java.util.List;

import es.academia.campushub.util.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Punto de entrada de la tarea. Sustituye cada stub por una implementación real. NO hay entidades,
 * repositorios ni documentos entregados.
 */
@Service
public class CampusHubServiceImpl implements CampusHubService {

    private final DepartamentoRepository departamentos;
    private final DocenteRepository docentes;
    private final GrupoRepository grupos;
    private final EstudianteRepository estudiantes;
    private final ActividadRepository actividades;
    private final ContactoEstudianteRepository contactos;

    public CampusHubServiceImpl(
            DepartamentoRepository departamentos,
            DocenteRepository docentes,
            GrupoRepository grupos,
            EstudianteRepository estudiantes,
            ActividadRepository actividades,
            ContactoEstudianteRepository contactos) {
        this.departamentos = departamentos;
        this.docentes = docentes;
        this.grupos = grupos;
        this.estudiantes = estudiantes;
        this.actividades = actividades;
        this.contactos = contactos;
    }

  @Override
  public GrupoResponse crearGrupo(CreateGrupoRequest request) {
      if (request == null) {
          throw new InvalidInputException("Cuerpo obligatorio");
      }
      String nombre = Validaciones.texto(request.nombre(), "nombre", 100);
      String nivel = Validaciones.texto(request.nivel(), "nivel", 50);
      Docente tutor = docentes
                      .findById(Validaciones.id(request.tutorDocenteId(), "tutorDocenteId"))
                      .orElseThrow(() -> new ResourceNotFoundException("Docente tutor no encontrado"));
      Grupo g = new Grupo();
      g.setNombre(nombre);
      g.setNivel(nivel);
      g.setTutor(tutor);
      return Mapper.toResponse(grupos.saveAndFlush(g));
  }

  @Override
  public DepartamentoResponse crearDepartamento(CreateDepartamentoRequest request) {
      String nombre = Validaciones.texto(request == null ? null : request.nombre(), "nombre", 100);
      if (departamentos.existsByNombre(nombre)) {
          throw new BusinessConflictException("Ya existe un departamento con ese nombre");
      }
      Departamento d = new Departamento();
      d.setNombre(nombre);
      return Mapper.toResponse(departamentos.saveAndFlush(d));
  }

  @Override
  public EstudianteResponse crearEstudiante(CreateEstudianteRequest request) {
      if (request == null) {
          throw new InvalidInputException("Cuerpo obligatorio");
      }
      String nombre = Validaciones.texto(request.nombre(), "nombre", 80);
      String apellidos = Validaciones.texto(request.apellidos(), "apellidos", 120);
      String email = Validaciones.email(request.email(), "email");
      LocalDate nacimiento = Validaciones.fecha(request.fechaNacimiento(), "fechaNacimiento");
      if (!nacimiento.isBefore(LocalDate.now())) {
          throw new InvalidInputException("fechaNacimiento debe ser anterior a hoy");
      }
      Grupo grupo = grupos
                      .findById(Validaciones.id(request.grupoId(), "grupoId"))
                      .orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado"));
      if (estudiantes.existsByEmail(email)) {
          throw new BusinessConflictException("Ya existe un estudiante con ese correo");
      }
      Estudiante e = new Estudiante();
      e.setNombre(nombre);
      e.setApellidos(apellidos);
      e.setEmail(email);
      e.setFechaNacimiento(nacimiento);
      e.setGrupo(grupo);
      return Mapper.toResponse(estudiantes.saveAndFlush(e));
  }

  @Override
  public EstudianteResponse obtenerEstudiante(Long id) {
      return Mapper.toResponse(estudiante(id));
  }

  @Override
  public DocenteResponse crearDocente(CreateDocenteRequest request) {
      if (request == null) {
          throw new InvalidInputException("Cuerpo obligatorio");
      }
      String nombre = Validaciones.texto(request.nombre(), "nombre", 80);
      String apellidos = Validaciones.texto(request.apellidos(), "apellidos", 120);
      String email = Validaciones.email(request.email(), "email");
      Departamento departamento = departamentos
                      .findById(Validaciones.id(request.departamentoId(), "departamentoId"))
                      .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado"));
      if (docentes.existsByEmail(email)) {
          throw new BusinessConflictException("Ya existe un docente con ese correo");
      }
      Docente d = new Docente();
      d.setNombre(nombre);
      d.setApellidos(apellidos);
      d.setEmail(email);
      d.setDepartamento(departamento);
      return Mapper.toResponse(docentes.saveAndFlush(d));
  }

  @Override
  public ActividadResponse crearActividad(CreateActividadRequest request) {
      if (request == null) {
          throw new InvalidInputException("Cuerpo obligatorio");
      }
      String titulo = Validaciones.texto(request.titulo(), "titulo", 150);
      String descripcion = Validaciones.textoLargo(request.descripcion(), "descripcion");
      String tipo = Validaciones.texto(request.tipo(), "tipo", 30);
      LocalDate fecha = Validaciones.fecha(request.fechaPrevista(), "fechaPrevista");
      if (request.maxParticipantes() == null || request.maxParticipantes() <= 0) {
          throw new InvalidInputException("maxParticipantes debe ser mayor que cero");
      }
      Docente docente = docentes
                      .findById(Validaciones.id(request.docenteId(), "docenteId"))
                      .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado"));
      Actividad a = new Actividad();
      a.setTitulo(titulo);
      a.setDescripcion(descripcion);
      a.setTipo(tipo);
      a.setFechaPrevista(fecha);
      a.setMaxParticipantes(request.maxParticipantes());
      a.setDocente(docente);
      return Mapper.toResponse(actividades.saveAndFlush(a));
  }

  @Override
  public ActividadResponse obtenerActividad(Long id) {
      Actividad a = actividades
                      .findWithDocenteById(id)
                      .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));
      return Mapper.toResponse(a);
  }

  @Override
  public InscripcionResponse inscribir(Long actividadId, CreateInscripcionRequest request) {
    throw new PersistenciaPendienteException("inscribir");
  }

  @Override
  public List<EstudianteResponse> participantes(Long actividadId) {
    throw new PersistenciaPendienteException("participantes");
  }

  @Override
  public SeguimientoResponse registrarSeguimiento(
      Long actividadId, CreateSeguimientoRequest request) {
    throw new PersistenciaPendienteException("registrarSeguimiento");
  }

  @Override
  public ActividadDetalleResponse detalle(Long id) {
    throw new PersistenciaPendienteException("detalle");
  }

  @Override
  public List<ActividadHistorialResponse> historial(Long estudianteId) {
    throw new PersistenciaPendienteException("historial");
  }

  @Override
  public ContactoResponse guardarContacto(Long estudianteId, UpsertContactoRequest request) {
      if (request == null) {
          throw new InvalidInputException("Cuerpo obligatorio");
      }
      String tutor = Validaciones.texto(request.tutorLegal(), "tutorLegal", 160);
      String telefono = Validaciones.texto(request.telefono(), "telefono", 20);
      String email = Validaciones.email(request.email(), "email");
      if (request.autorizado() == null) {
          throw new InvalidInputException("La autorizacion es obligatoria");
      }
      LocalDate fecha = Validaciones.fechaOpcional(request.fechaAutorizacion(), "fechaAutorizacion");
      Estudiante estudiante = estudiante(estudianteId);
      ContactoEstudiante c = contactos.findById(estudianteId).orElseGet(ContactoEstudiante::new);
      c.setEstudiante(estudiante);
      c.setTutorLegal(tutor);
      c.setTelefono(telefono);
      c.setEmail(email);
      c.setAutorizado(request.autorizado());
      c.setFechaAutorizacion(fecha);
      return Mapper.toResponse(contactos.saveAndFlush(c));  }

    private Estudiante estudiante(Long id) {
        return estudiantes
                .findWithGrupoById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));
    }

  @Override
  public ContactoResponse contacto(Long estudianteId) {
      estudiante(estudianteId);
      return contactos
              .findById(estudianteId)
              .map(Mapper::toResponse)
              .orElseThrow(() -> new ResourceNotFoundException("El estudiante no tiene ficha"));  }

  @Override
  public EtiquetaResponse crearEtiqueta(CreateEtiquetaRequest request) {
    throw new PersistenciaPendienteException("crearEtiqueta");
  }

  @Override
  public void asociarEtiqueta(Long actividadId, Long etiquetaId) {
    throw new PersistenciaPendienteException("asociarEtiqueta");
  }

  @Override
  public List<ActividadResponse> actividadesPorEtiqueta(Long etiquetaId) {
    throw new PersistenciaPendienteException("actividadesPorEtiqueta");
  }
}
