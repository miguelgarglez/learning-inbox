# Testing strategy

## Current evidence

`mvn verify` runs ResourceApiTest through Maven Surefire and packages the application.
The suite has 12 cases, including eight parameterized invalid-input cases.

- Real embedded server; random local port.
- Java HTTP client; assertions on status, headers and JSON.
- New application context per case to isolate memory state.
- No database, external HTTP dependency, mocks or external load target.

Covered behaviors: create and retrieve identical data, title normalization,
server-assigned IDs and timestamps, optional reason, distinct IDs, length limits,
invalid URLs, malformed JSON, missing fields and missing/malformed identifiers.

JUnit failures fail the Maven build and CI. Reports are generated in
`target/surefire-reports/`; generated reports are not committed.

## What is not yet demonstrated

No persistence across restarts, authorization, transactional rollback, concurrency
invariants, load capacity or failure recovery is claimed. Validation precedes
insertion; the HTTP suite does not directly inspect internal storage to assert
the absence of every invalid write.

## Planned experiments

| Trigger | Property to verify | Evidence |
| --- | --- | --- |
| Concurrent duplicate submissions | One resource per owner and normalized URL | Responses and database state |
| Failure midway through a transaction | No partial update | State after rollback |
| Another user's resource ID | No disclosure or modification | Response and unchanged data |
| Retry after uncertain response | Defined duplicate/idempotency behavior | Stored result and repeated responses |
| Sustained local load | Explicit latency/error target for a defined workload | Script, environment and metrics |

These are roadmap items, not passing tests. Load testing stays separate from fast
PR checks and requires an explicitly authorized target.
