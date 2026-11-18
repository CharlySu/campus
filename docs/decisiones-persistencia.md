# Decisiones de persistencia

## 1. Qué va en PostgreSQL y qué en MongoDB

| Datos | Dónde | Por qué |
|---|---|---|
| Departamento, docente, grupo, estudiante, actividad, inscripción, contacto y etiqueta | PostgreSQL | Tienen una estructura fija y están muy relacionados entre sí. Necesitamos claves foráneas, campos únicos y transacciones. |
| Seguimientos de actividad | MongoDB | El campo `datos` puede tener cosas distintas según el tipo de seguimiento (en una visita la empresa y las observaciones, en otro tipo otros campos). En MongoDB se guarda tal cual, sin crear columnas nuevas. |

Cómo trabajan juntas las dos bases de datos:

- Antes de guardar un seguimiento en MongoDB comprobamos en PostgreSQL que la actividad existe.
  Si no existe devolvemos 404 y no se guarda nada.
- El seguimiento solo guarda el `actividadId`. No puede haber clave foránea entre dos bases de datos
  distintas.
- Los seguimientos solo se añaden, nunca se modifican ni se borran, así que no necesitamos
  transacciones entre las dos bases de datos.
- El endpoint `/api/actividades/{id}/detalle` junta los datos de las dos: actividad, docente,
  inscritos y etiquetas vienen de PostgreSQL, y los seguimientos de MongoDB.

El documento `Seguimiento` tiene estos campos: `id`, `actividadId`, `tipo`, `creadoEn`,
`schemaVersion` y `datos` (un mapa con contenido libre).

## 2. Relaciones entre tablas

```text
Departamento 1 ── N Docente
Docente      1 ── N Grupo          (el docente es el tutor)
Docente      1 ── N Actividad      (el docente es el responsable)
Grupo        1 ── N Estudiante
Estudiante   1 ── 1 ContactoEstudiante   (no es obligatorio que tenga)
Estudiante   1 ── N Inscripcion
Actividad    1 ── N Inscripcion
Actividad    N ── N Etiqueta       (tabla actividad_etiqueta)
```

Cosas que decidimos:

- `Inscripcion` es una entidad y no un `@ManyToMany` porque guarda la fecha de inscripción.
- `ContactoEstudiante` usa como clave primaria el id del estudiante (`@MapsId`). Así un estudiante
  solo puede tener una ficha.
- Actividad y etiqueta sí son `@ManyToMany`, porque la relación no guarda más datos.
- Todas las relaciones son `LAZY`. Cuando necesitamos los datos relacionados usamos `join fetch` o
  `@EntityGraph`.

## 3. Restricciones en la base de datos

Además de validar en el servicio, las reglas están también en la base de datos. Así no se pueden
meter datos mal aunque se salten la API.

| Tabla | UNIQUE | Clave foránea | CHECK |
|---|---|---|---|
| departamento | nombre | | nombre no vacío |
| docente | email | departamento_id | nombre y apellidos no vacíos, email con `@` |
| grupo | | tutor_docente_id | nombre y nivel no vacíos |
| estudiante | email | grupo_id | nombre y apellidos no vacíos, email con `@`, fecha de nacimiento anterior a hoy |
| contacto_estudiante | | estudiante_id | tutor y teléfono no vacíos, email con `@` |
| actividad | | docente_id | título, tipo y descripción no vacíos, `max_participantes > 0` |
| inscripcion | (actividad_id, estudiante_id) | las dos | |
| etiqueta | nombre | | nombre no vacío |

También hemos puesto índices en las claves foráneas que más se consultan.

## 4. Aforo y concurrencia

El método `inscribir` es `@Transactional` y lee la actividad con bloqueo `PESSIMISTIC_WRITE`. Si
dos personas intentan coger la última plaza a la vez, la segunda espera a que acabe la primera, ve
que ya no hay plazas y recibe un 409.

Para que no se pueda inscribir dos veces el mismo estudiante lo comprobamos en el servicio y
además existe el índice único `(actividad_id, estudiante_id)`.

Asociar una etiqueta a una actividad se puede repetir sin problema: usamos `ON CONFLICT DO NOTHING`
y siempre devuelve 204.

## 5. Errores

| Código | Cuándo |
|---|---|
| 400 | JSON mal formado, campos vacíos, email o fecha incorrectos, fecha de nacimiento futura, aforo 0 o negativo, claves no válidas en `datos` |
| 404 | No existe el estudiante, docente, departamento, grupo, actividad o etiqueta, o el estudiante no tiene ficha de contacto |
| 409 | Email, departamento o etiqueta repetidos, inscripción repetida o actividad sin plazas |

Usamos `InvalidInputException` (400), `ResourceNotFoundException` (404) y `BusinessConflictException`
(409). Si algo llega a la base de datos y la restricción falla, el handler que venía en el proyecto
base devuelve 409 sin enseñar el SQL.

Las validaciones están en el servicio (`util/Validaciones`) porque los DTO del proyecto son
`record` sin anotaciones y no podíamos cambiarlos.

## 6. Cosas a tener en cuenta

- Usamos `ddl-auto=update` solo para desarrollo. En un proyecto real habría que usar Flyway o
  Liquibase.
- `update` no añade los `CHECK` a tablas que ya existían. Si antes se arrancó el proyecto con otra
  versión, hay que borrar los datos con `docker compose down -v`.
- Los tests crean contenedores propios con Testcontainers. Ponemos `reuseForks=false` en failsafe
  para que cada clase de test arranque los suyos.

## 7. Lo que se podría mejorar

- No hemos puesto validación de esquema en MongoDB. Solo se controla desde el servicio.
- Tampoco hay índice en MongoDB. Si hubiera muchos seguimientos, convendría crear uno por
  `actividadId`.
- Las claves de `datos` no pueden estar vacías, tener un punto ni empezar por `$`, porque MongoDB
  y Spring Data no lo permiten. En esos casos devolvemos 400.
- No hay autenticación ni paginación.
- No hemos usado Redis.