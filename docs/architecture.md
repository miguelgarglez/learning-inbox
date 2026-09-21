# Architecture

One Spring Boot application exposes an HTTP API on loopback. Resources live in
PostgreSQL and belong to an authenticated user identified by a Bearer API key.
There is no OAuth provider, external HTTP dependency or background worker.

| Component | Responsibility |
| --- | --- |
| LearningInboxApplication | Entry point; excludes default in-memory user auto-config. |
| SecurityConfig | Stateless filter chain; all requests authenticated. |
| ApiKeyAuthenticationFilter | Reads `Authorization: Bearer`, resolves user via JDBC. |
| ProblemDetailsAuthenticationEntryPoint | `401` as `application/problem+json`. |
| ApiKeyRepository | Token → `AuthenticatedUser`. |
| CreateResourceRequest | Incoming DTO, title normalization and field constraints. |
| ResourceController | HTTP routing; takes `@AuthenticationPrincipal`. |
| ResourceService | URL semantics, identity, timestamps, owner-scoped use of the repository. |
| ResourceRepository | SQL insert and owner-scoped lookup via `JdbcClient`. |
| LearningResource | Immutable resource data including `ownerId`. |
| ResourceExceptionHandler | Domain failures → Problem Details. |
| Flyway `V1` / `V2` | Resources table; users, api_keys, `owner_id`. |

## Request flow

For POST, Spring Security authenticates the Bearer key (or returns 401). Spring
MVC deserializes and validates the body, then the controller passes the principal's
id to the service. The service assigns resource id/status/timestamps, inserts with
`owner_id`, and responds 201 with Location.

For GET, after authentication the service reads `WHERE id = ? AND owner_id = ?`.
Missing or foreign rows become 404. Malformed ids remain 400.

## Boundaries and tradeoffs

- Ownership is a domain rule enforced in SQL, not only in the controller.
- Another user's resource is indistinguishable from missing (`404`) to avoid
  existence leaks.
- API keys are plaintext in the database for local learning; hashing and rotation
  are out of scope.
- URL uniqueness per owner waits for the concurrency milestone.
- Tests use Testcontainers Postgres and truncate `resources` between cases; users
  and keys stay as Flyway seeds.

## Next architectural change

Failure and concurrency experiments: races on duplicate submissions, retries and
invariants (including uniqueness per owner when introduced).

See [ADR 0003](adr/0003-private-resources-api-keys.md).
