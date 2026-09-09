# ADR 0001: Start with a single in-memory HTTP API

- Status: Accepted
- Date: 2026-09-09

## Context

The first learning goal is to understand the complete create-and-retrieve HTTP
flow in Java. Introducing a database, authentication and infrastructure together
would make failures harder to attribute to the concept being studied.

## Decision

Use Java 21, Spring Boot, one Maven module and feature packages. Store immutable
resources in a concurrent in-memory map. Bind to loopback and test with real HTTP.
Use installed Maven with documented requirements. A pinned Maven Wrapper remains
a future reproducibility improvement.

## Alternatives

- PostgreSQL immediately: durable and closer to deployment, but adds installation,
  migrations and lifecycle concerns before the HTTP path is understood.
- H2 as an intermediate database: adds a SQL engine whose behavior is not equivalent
  to the PostgreSQL intended for the next milestone.
- Multiple services or modules: adds boundaries without a current scaling or ownership need.

## Consequences

The first milestone is easy to run and inspect. Data is lost on restart and this
version is unsuitable for public deployment. Persistence, ownership and URL uniqueness
are deferred explicitly. Tests make no durability or capacity claim.
