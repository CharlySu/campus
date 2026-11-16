package es.academia.campushub.repository;

import es.academia.campushub.entity.Estudiante;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
  boolean existsByEmail(String email);

  @EntityGraph(attributePaths = {"grupo"})
  Optional<Estudiante> findWithGrupoById(Long id);
}
