# CampusHub

Proyecto de 2.º DAM. Es una API REST para gestionar actividades de un centro (visitas, talleres...),
con sus docentes, grupos, estudiantes e inscripciones. Usamos **PostgreSQL** para casi todos los
datos y **MongoDB** para los seguimientos de las actividades.

Partimos del proyecto base que nos dieron y hemos implementado la persistencia. Los controllers y
los DTO no los hemos tocado.

## Requisitos

- JDK 21 y Maven 3.9+
- Docker Desktop (para las bases de datos y para los tests)

## Cómo arrancarlo

```bash
mvn spring-boot:run
```

Spring Boot levanta solo los contenedores del `compose.yaml`. Si queremos arrancarlos nosotros:

```bash
docker compose up -d
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.docker.compose.enabled=false
```

Para mirar las bases de datos:

```bash
docker compose exec postgres psql -U campushub -d campushub
docker compose exec mongo mongosh campushub
```

Con la aplicación arrancada, Swagger está en <http://localhost:8080/swagger-ui.html>.

## Estructura

```text
controller/       endpoints REST (no los hemos modificado)
controller/dto/   DTO de entrada y salida (no los hemos modificado)
service/          lógica de negocio
repository/       repositorios de Spring Data (JPA y MongoDB)
entity/           entidades JPA (PostgreSQL)
document/         documento de MongoDB (Seguimiento)
mapper/           pasa de entidades/documentos a DTO
util/             validaciones y excepciones
docs/             documentación
```

## Tests

```bash
mvn test                # tests unitarios
mvn verify              # tests de integración (necesitan Docker) y formato
mvn spotless:apply      # formatear el código antes de verify
```

Los tests usan Testcontainers, así que crean sus propias bases de datos y no tocan las del
`compose.yaml`.

## Documentación

- [`docs/api-contract.md`](docs/api-contract.md): endpoints y códigos de respuesta
- [`docs/decisiones-persistencia.md`](docs/decisiones-persistencia.md): qué va en PostgreSQL y qué en MongoDB, y por qué
- [`docs/ejemplos-api.md`](docs/ejemplos-api.md): ejemplos para probar la API

## Resumen de lo que hemos decidido

- **PostgreSQL:** departamentos, docentes, grupos, estudiantes, actividades, inscripciones,
  fichas de contacto y etiquetas.
- **MongoDB:** seguimientos de las actividades, porque el campo `datos` cambia según el tipo.
- **Aforo:** al inscribir bloqueamos la actividad para que dos personas no cojan la última plaza.
- **Errores:** 400 si los datos están mal, 404 si no existe algo y 409 si hay duplicados o no hay plazas.
- **Redis:** no lo hemos usado porque era opcional.