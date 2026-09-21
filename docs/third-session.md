# Sesión 3: recursos privados (API key)

## Objetivo

Explicar la diferencia entre autenticación (quién llama) y autorización /
ownership (qué puede ver), con el contrato HTTP del inbox.

## Antes de implementar — predicciones (Miguel)

1. POST sin `Authorization` → **401** (no es lo mismo que título vacío → **400**).
2. Bob GET del id de Alice → **404**, no 403 (no filtrar existencia).
3. `owner_id` lo asigna el **servidor** a partir de la key, no el JSON del cliente.

## Qué es automático vs explícito

| Automático (Spring Security / Boot) | Explícito (nuestro código) |
| --- | --- |
| Filter chain; exige autenticación en `/api/**` | Migración `V2`: `users`, `api_keys`, `resources.owner_id` |
| `SecurityContext` con el principal tras el filtro | Lookup JDBC de Bearer → `AuthenticatedUser` |
| Entry point → `401` Problem Details | `INSERT`/`SELECT` con `owner_id`; ajeno → 404 |
| | Seeds alice/bob y keys de desarrollo |

## Recorrido local

```sh
docker compose up -d
# Si la BD local quedó en esquema v1 con filas raras: docker compose down -v && docker compose up -d
export JAVA_HOME="$(/usr/libexec/java_home -v 21)"
export PATH="$JAVA_HOME/bin:$PATH"
mvn spring-boot:run
```

```sh
curl -i http://127.0.0.1:8080/api/resources \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer li_alice_dev_key_001' \
  --data-binary @requests/create-resource.json
```

GET con la misma key al `Location`. Repite el GET con `li_bob_dev_key_002`:
debe ser `404`.

Bruno: entorno **local** ya lleva `apiKey` de Alice.

## Estado de implementación — 2026-09-15

- `spring-boot-starter-security` + filter Bearer + Problem Details en 401.
- Ownership en servicio/repositorio; respuesta JSON incluye `ownerId`.
- `mvn verify`: 17 pruebas (incluye 401 y recurso ajeno → 404).
- Unicidad `(owner, url)` y hashing de keys: fuera de este hito.
