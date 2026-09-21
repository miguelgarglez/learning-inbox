# Testing strategy

## Current evidence

`mvn verify` runs ResourceApiTest and ResourceDurabilityTest through Maven
Surefire and packages the application. The suite has **17** cases, including
eight parameterized invalid-input cases.

- Real embedded server; random local port.
- Java HTTP client with `Authorization: Bearer` on authenticated calls.
- Disposable PostgreSQL via Testcontainers and `@ServiceConnection`.
- ResourceApiTest clears the `resources` table before each case (users/keys seeded).
- ResourceDurabilityTest recreates the Spring context against the same container
  and retrieves the previously created resource over HTTP.
- ResourceApiTest asserts `owner_id` in PostgreSQL after POST.
- Cases cover missing/invalid API key (`401`) and cross-user GET (`404`).
- Invalid input cases assert that no row was inserted.
- No mocks of the repository; no external load target.

Covered behaviors: create and retrieve identical data, ownership, title
normalization, server-assigned IDs and timestamps, optional reason, distinct IDs,
length limits, invalid URLs, malformed JSON, missing fields, missing/malformed
identifiers, unauthorized callers, foreign resource ids, PostgreSQL persistence
and survival across application context reload.

JUnit failures fail the Maven build and CI. Reports are generated in
`target/surefire-reports/`; generated reports are not committed. Docker must be
available for Testcontainers (local and CI).

## What is not yet demonstrated

No transactional rollback of multi-step workflows, concurrency invariants, load
capacity, key hashing or failure recovery is claimed. Validation precedes
insertion; database constraints are an additional safety net exercised indirectly.

## Planned experiments

| Trigger | Property to verify | Evidence |
| --- | --- | --- |
| Concurrent duplicate submissions | One resource per owner and normalized URL | Responses and database state |
| Failure midway through a transaction | No partial update | State after rollback |
| Retry after uncertain response | Defined duplicate/idempotency behavior | Stored result and repeated responses |
| Sustained local load | Explicit latency/error target for a defined workload | Script, environment and metrics |

These are roadmap items, not passing tests. Load testing stays separate from fast
PR checks and requires an explicitly authorized target.
