# Contributing

This is a deliberately small learning project. Keep changes focused on one behavior
and discuss substantial scope changes in an issue first.

## Local workflow

1. Use JDK 21, Maven 3.9+ and Docker; see [tooling](docs/java-tooling.md).
2. For manual runs: `docker compose up -d` before `mvn spring-boot:run`.
3. Create a short-lived branch from main.
4. Describe the behavior and acceptance criteria before implementation.
5. Add or update meaningful tests for changed behavior.
6. Run `mvn verify` (needs Docker for Testcontainers) and `git diff --check`.
7. Open a pull request describing the result, verification and limitations.

Prefer immutable data, constructor injection, feature packages and explicit errors.
Follow existing Java formatting: four spaces, no wildcard imports.
Update the contract when API behavior changes. Record architectural tradeoffs in
an ADR when a real choice is made.

## AI-assisted changes

Read [AGENTS.md](AGENTS.md). Review generated code, verify claims and identify remaining
uncertainty. Never report tests as passing unless executed. Keep personal context,
secrets, generated artifacts and machine-specific paths out of commits.
Do not paste private conversations into issues or pull requests.

## Scope

The current milestone is local and unauthenticated, with PostgreSQL persistence.
Security, load testing and deployment are separate steps. New dependencies require
an explanation and maintainer approval before local installation.
