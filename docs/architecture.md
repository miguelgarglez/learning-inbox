# Architecture

One Spring Boot application exposes an HTTP API on loopback and stores resources
in PostgreSQL. There is no authentication provider, external HTTP dependency or
background worker.

| Component | Responsibility |
| --- | --- |
| LearningInboxApplication | Entry point and Spring configuration discovery. |
| CreateResourceRequest | Incoming DTO, title normalization and field constraints. |
| ResourceController | HTTP routing, status codes and Location. |
| ResourceService | URL semantics, identity, timestamps and transactional use of the repository. |
| ResourceRepository | SQL insert and lookup via `JdbcClient`. |
| LearningResource | Immutable resource data and initial status. |
| ResourceExceptionHandler | Translate expected failures into Problem Details. |
| Flyway migration `V1__create_resources` | Versioned table definition and database constraints. |

## Request flow

For POST, Spring deserializes JSON into the request record, validates its fields,
and invokes the controller. The service checks URL semantics, assigns id/status
timestamps, and inserts inside a transaction. The controller responds with 201
and a relative Location path.

For GET, Spring converts the path parameter to UUID, the service reads through
the repository, and the result becomes JSON. Missing resources return 404.
Invalid JSON, invalid fields and malformed identifiers return 400.

## Boundaries and tradeoffs

- PostgreSQL is the system of record. Restarting the JVM does not erase rows
  while the database remains available.
- Flyway owns schema evolution. Application code does not create tables ad hoc.
- Database CHECKs and NOT NULL reinforce HTTP validation; they are a safety net,
  not a replacement for Bean Validation at the API boundary.
- `@Transactional` on the service marks the unit of work for create/find. A
  single INSERT failure rolls back that unit; richer multi-step rollback cases
  are still roadmap experiments.
- Tests use Testcontainers Postgres and truncate (or ordered context reload) for
  isolation instead of relying on an empty in-memory map.
- Local binding limits exposure in this unauthenticated milestone. URL uniqueness
  per owner waits for authentication.

## Next architectural change

Introduce ownership and authentication. Choose authorization and uniqueness
rules with that model rather than adding generic security frameworks early.

See [ADR 0002](adr/0002-persist-with-postgresql-flyway-jdbc.md).
