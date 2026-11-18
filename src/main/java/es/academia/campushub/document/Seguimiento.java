package es.academia.campushub.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Document(collection = "seguimientos")
@Getter
@Setter
@NoArgsConstructor
public class Seguimiento {
  @Id private String id;

  private Long actividadId;

  private String tipo;

  private Instant creadoEn;

  private int schemaVersion = 1;

  private Map<String, Object> datos;
}
