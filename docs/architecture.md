# Architecture

One Spring Boot application exposes an HTTP API on loopback. There is no database,
authentication provider, external HTTP dependency or background worker.

| Component | Responsibility |
| --- | --- |
| LearningInboxApplication | Entry point and Spring configuration discovery. |
| CreateResourceRequest | Incoming DTO, title normalization and field constraints. |
| ResourceController | HTTP routing, status codes and Location. |
| ResourceService | URL semantics, creation, identity, timestamp and lookup. |
| LearningResource | Immutable resource data and initial status. |
| ResourceExceptionHandler | Translate expected failures into Problem Details. |

## Request flow

For POST, Spring deserializes JSON into the request record, validates its fields,
and invokes the controller. The service checks URL semantics before storing the
resource. The controller responds with 201 and a relative Location path.

For GET, Spring converts the path parameter to UUID, the service looks up the
resource, and the result becomes JSON. Missing resources return 404. Invalid JSON,
invalid fields and malformed identifiers return 400.

## Boundaries and tradeoffs

- ConcurrentHashMap supports concurrent individual map operations. It does not make
  a multi-step business operation atomic or enforce unique URLs.
- Resources are immutable records. The API response uses this resource model directly;
  a persistence model and mapping can be introduced when storage requires it.
- Validation annotations run at the HTTP boundary. The service assumes calls originate
  there; future non-HTTP entry points must preserve validation.
- Recreating the test context isolates state at the cost of startup work.
- Local binding limits exposure in this unauthenticated milestone. Deployment requires
  explicit decisions about access control, storage and configuration.

## Next architectural change

Introduce PostgreSQL with migrations and isolated database tests. Choose storage
boundaries based on that implementation rather than creating generic repository
abstractions before they are useful.
