package es.academia.campushub.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * Inscripción de un estudiante en una actividad. Es una entidad propia (y no un @ManyToMany) porque
 * almacena un dato adicional: la fecha de inscripción.
 *
 * <p>La unicidad de la pareja (actividad, estudiante) y las claves foráneas las garantiza la base
 * de datos, no solo el servicio.
 */
@Entity
@Table(
    name = "inscripcion",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_inscripcion_actividad_estudiante",
            columnNames = {"actividad_id", "estudiante_id"}),
    indexes = @Index(name = "ix_inscripcion_estudiante", columnList = "estudiante_id"))
@Getter
@Setter
public class Inscripcion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "actividad_id", nullable = false)
  private Actividad actividad;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "estudiante_id", nullable = false)
  private Estudiante estudiante;

  @Column(name = "fecha_inscripcion", nullable = false)
  private LocalDateTime fechaInscripcion;

  public Inscripcion() {}
}
