# ADR 0003: Private resources with API keys

- Status: Accepted
- Date: 2026-09-15

## Context

Milestones 1–2 exposed create/retrieve without identity. The product goal is a
personal learning inbox: each resource belongs to a user, and another caller
must not read it. Full OAuth/JWT would bury ownership under token machinery.

## Decision

Authenticate with a Bearer API key resolved against PostgreSQL (`api_keys` →
`users`). Use Spring Security for a stateless filter chain and `401` when there
is no valid principal. Store `owner_id` on `resources` and scope SELECT/INSERT
to that owner. Treat another user's id as `404`, same as missing.

Seed two local users (alice/bob) with plaintext keys for development and tests.
Do not hash keys or add registration in this milestone.

## Alternatives

- HTTP Basic with passwords: more ceremony (hashing) without teaching ownership
  better than API keys.
- JWT/OAuth2 resource server: industry-common later; heavy for a local inbox.
- Custom servlet filter without Spring Security: possible, but loses the standard
  security context and entry-point patterns used in production Spring apps.

## Consequences

Every `/api/**` call requires `Authorization: Bearer …`. Responses include
`ownerId`. URL uniqueness per owner remains deferred. Plaintext keys are a
documented learning limit, not a production pattern.
