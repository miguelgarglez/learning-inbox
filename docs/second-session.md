# Sesión 2: persistir en PostgreSQL

## Objetivo

Explicar qué ocurre entre un POST válido y una fila en PostgreSQL, y por qué un
GET sigue funcionando después de reiniciar la aplicación (con la base aún viva).

## Antes de implementar — predicciones

Responde antes de mirar el código:

1. ¿Quién crea las tablas: la app al arrancar, un script manual, o ambos roles
   vía Flyway?
2. Si el `INSERT` falla a mitad, ¿qué debe pasar con la transacción?
3. Tras `Ctrl+C` y volver a `spring-boot:run`, ¿por qué el GET puede seguir
   funcionando?

## Qué es automático vs explícito

| Automático (Spring Boot / herramientas) | Explícito (nuestro código) |
| --- | --- |
| `DataSource` y pool Hikari a partir de `spring.datasource.*` | SQL de `INSERT` / `SELECT` en `ResourceRepository` |
| Flyway aplica `db/migration/V1__*.sql` al arrancar | Reglas de URL en `ResourceService` |
| `JdbcClient` como bean | `@Transactional` en create/find |
| Testcontainers + `@ServiceConnection` cablean el JDBC de test | Contrato HTTP y Problem Details (igual que hito 1) |

## Recorrido local

Requiere Docker Desktop en marcha.

```sh
docker compose up -d
export JAVA_HOME="$(/usr/libexec/java_home -v 21)"
export PATH="$JAVA_HOME/bin:$PATH"
mvn spring-boot:run
```

En otra terminal: POST con `requests/create-resource.json`, anota el `Location`,
detén la app (Ctrl+C), vuelve a arrancarla y haz GET a esa ruta. Los datos deben
seguir ahí porque viven en Postgres, no en la JVM.

Para pruebas: `mvn verify` levanta Postgres desechable con Testcontainers (también
necesita Docker). No hace falta `docker compose` para el verify.

Opcional: colección Bruno en `bruno/learning-inbox/` (Create → Get con `resourceId`).

## Estado de implementación — 2026-09-14

- Dependencias JDBC, Flyway, driver PostgreSQL y Testcontainers añadidas.
- Migración `V1__create_resources.sql` con constraints de longitud/status.
- `ResourceRepository` + `ResourceService` sin mapa en memoria.
- `mvn verify`: 15 pruebas, sin fallos (incluye aserción SQL y recarga de contexto).
- Manual Compose + reinicio de app: comprobar en tu máquina con los comandos de arriba.

No demuestra autenticación, unicidad por propietario, rollback forzado en fallos
parciales multi-paso, ni capacidad bajo carga.
