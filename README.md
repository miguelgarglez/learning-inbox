# Learning Inbox

**Save something worth learning. Understand the backend behind it.**

[![CI](https://github.com/miguelgarglez/learning-inbox/actions/workflows/ci.yml/badge.svg)](https://github.com/miguelgarglez/learning-inbox/actions/workflows/ci.yml)
![Java 21](https://img.shields.io/badge/Java-21-437291)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1.1-6DB33F)

A small Java API for collecting learning resources, built incrementally to explore
backend engineering through working software, explicit decisions and reproducible tests.

**Current milestone:** private resources — Bearer API keys, ownership in PostgreSQL,
authorization tests. Local learning project with seeded alice/bob keys (not production auth).

## Try it

Requires **JDK 21**, **Maven 3.9+** and **Docker** (Docker Desktop is fine).
Check `java -version`, `mvn -version` and `docker version`.
[Java setup and Maven commands (Spanish)](docs/java-tooling.md).

```sh
git clone https://github.com/miguelgarglez/learning-inbox.git
cd learning-inbox
docker compose up -d
mvn verify
mvn spring-boot:run
```

If you already had a Compose volume from milestone 2, recreate it once so Flyway
can apply ownership (`docker compose down -v && docker compose up -d`).

The API listens on `http://127.0.0.1:8080`. In another terminal:

```sh
curl -i http://127.0.0.1:8080/api/resources \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer li_alice_dev_key_001' \
  --data-binary @requests/create-resource.json
```

Example response (ID and timestamp vary):

```http
HTTP/1.1 201 Created
Location: /api/resources/7aeea7eb-505f-4eed-b163-c72b4fbf5958
Content-Type: application/json
```

```json
{
  "id": "7aeea7eb-505f-4eed-b163-c72b4fbf5958",
  "ownerId": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
  "title": "Transacciones en PostgreSQL",
  "url": "https://www.postgresql.org/docs/current/tutorial-transactions.html",
  "reason": "Entender qué ocurre cuando falla una escritura",
  "status": "PENDING",
  "createdAt": "2026-09-09T18:00:00Z"
}
```

Retrieve it with `curl -i` plus the **Location** path and the same
`Authorization` header. Stop the app with Ctrl+C; start it again and GET again —
the row remains while Postgres is running. Dev keys: `li_alice_dev_key_001` /
`li_bob_dev_key_002`. Stop Postgres with `docker compose down` when done. There
is no web page at `/`.

## API at a glance

| Request | Success | Expected errors |
| --- | --- | --- |
| `POST /api/resources` + Bearer | `201` with resource and Location | `401` without/invalid key; `400` invalid input |
| `GET /api/resources/{id}` + Bearer | `200` with own resource | `401`; `400` malformed ID; `404` absent or another user's |

Titles are trimmed and limited to 200 characters. URLs must be absolute HTTP(S)
URLs with a host and no embedded credentials. Links are stored without fetching
them. Errors use `application/problem+json`.

[Full contract (Spanish)](docs/product.md) · [Manual requests](requests/README.md) ·
[Bruno collection](bruno/learning-inbox/README.md)

## How it works

```mermaid
flowchart LR
    Client[HTTP client] --> Security[API key filter]
    Security --> Validation[JSON and field validation]
    Validation --> Controller[ResourceController]
    Controller --> Service[ResourceService]
    Service --> Repository[ResourceRepository]
    Repository --> Postgres[(PostgreSQL)]
    Flyway[Flyway] --> Postgres
    Controller -. errors .-> Problems[Problem Details]
```

One application, one Maven module, packages organized by feature. Spring Security
authenticates the Bearer key; the service assigns and enforces `owner_id`. Spring
also provides HTTP routing, validation, DI, JDBC and Flyway; the repository runs SQL.

[Architecture and tradeoffs](docs/architecture.md) ·
[ADR: in-memory start](docs/adr/0001-start-with-an-in-memory-api.md) ·
[ADR: PostgreSQL + Flyway + JDBC](docs/adr/0002-persist-with-postgresql-flyway-jdbc.md) ·
[ADR: API keys and ownership](docs/adr/0003-private-resources-api-keys.md)

## Verification

```sh
mvn verify                           # tests + executable JAR (needs Docker)
mvn -Dtest=ResourceApiTest test
java -jar target/learning-inbox-0.0.1-SNAPSHOT.jar   # needs Compose Postgres up
```

The suite contains **17 test cases**. It starts a real embedded HTTP server on a
random port and a disposable Postgres via Testcontainers. It checks round-trip
data, ownership, `401`/`404` authorization boundaries, validation, SQL persistence
and survival across Spring context reload.

GitHub Actions runs `mvn verify` with Java 21 on pushes to `main` and pull requests.
[Testing strategy and its limits](docs/testing.md).

## Learning roadmap

| Milestone | Status | Engineering focus |
| --- | --- | --- |
| Create → retrieve | Implemented | Java, HTTP, validation, automated tests |
| Persistent resources | Implemented | PostgreSQL, migrations, constraints, transactions |
| Private resources | Implemented | Authentication, ownership, authorization tests |
| Failure and concurrency experiments | Planned | Races, retries, recovery, invariants |
| Operate and measure | Planned | Reproducible load, observability, deployment |

Notes and Markdown export are future product features. They are not implemented yet.

## Working with AI agents

AI assistance is part of development. [AGENTS.md](AGENTS.md) defines project boundaries
and verification expectations. Changes should state the problem, explain meaningful
tradeoffs and provide evidence from checks actually executed. Generated code is
reviewed against the product contract; tests are evidence for tested behavior,
not proof of production readiness.

Learning guides are in Spanish: [first session](docs/first-session.md),
[second session](docs/second-session.md),
[third session](docs/third-session.md),
[Java tooling](docs/java-tooling.md), [domain glossary](docs/glossary.md).

See [CONTRIBUTING.md](CONTRIBUTING.md) for the contribution workflow.
