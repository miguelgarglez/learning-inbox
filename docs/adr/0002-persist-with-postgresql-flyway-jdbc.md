# ADR 0002: Persist with PostgreSQL, Flyway and JDBC

- Status: Accepted
- Date: 2026-09-14

## Context

Milestone 1 stored resources in a ConcurrentHashMap. The next learning goal is
durable storage with an explicit schema, database constraints and transactions,
using the same HTTP contract.

Spring Data JPA is common for CRUD APIs, but it hides SQL, mapping and much of
the transactional boundary. Introducing JPA before those ideas are visible would
make failures harder to attribute.

## Decision

Use PostgreSQL as the system of record. Version the schema with Flyway. Access
data with Spring JDBC (`JdbcClient`) through a concrete `ResourceRepository`.
Run local development against Docker Compose Postgres. Run automated tests
against disposable Postgres via Testcontainers and `@ServiceConnection`.

Do not introduce JPA, Spring Data repositories or a generic persistence
abstraction in this milestone.

## Alternatives

- Spring Data JPA immediately: familiar industry pattern, but opaque for this
  learning stage; deferred for a later comparison on the same contract.
- H2 for tests: faster to start, but diverges from PostgreSQL behavior already
  rejected in ADR 0001.
- Hand-written SQL scripts without Flyway: works once, does not version schema
  history or support repeatable app startup cleanly.

## Consequences

Create and retrieve survive process restart when Postgres is still running.
Tests require Docker. Schema changes are migrations, not ad-hoc ALTER in app
code. URL uniqueness per owner remains deferred until authentication exists.
