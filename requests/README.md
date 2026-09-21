# Probar a mano

Con Docker Compose arriba (`docker compose up -d`) y el servidor arrancado,
desde la raíz del proyecto:

```sh
curl -i http://127.0.0.1:8080/api/resources \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer li_alice_dev_key_001' \
  --data-binary @requests/create-resource.json
```

La respuesta incluye `201` y una cabecera `Location` con el path del recurso.
Copia ese path después de `http://127.0.0.1:8080` y consúltalo con `curl -i`
usando la misma cabecera `Authorization`. Puedes detener la app, volver a
arrancarla y repetir el GET: el recurso sigue en PostgreSQL.

Prueba de aislamiento: el mismo GET con `li_bob_dev_key_002` debe ser `404`.

## Bruno

Colección versionada en [`bruno/learning-inbox/`](../bruno/learning-inbox/):
abre esa carpeta en Bruno, entorno **local** (incluye `apiKey`), ejecuta Create
y luego Get. Detalle en el README de la colección.

Para observar un error de validación (con key válida):

```sh
curl -i http://127.0.0.1:8080/api/resources/not-a-uuid \
  -H 'Authorization: Bearer li_alice_dev_key_001'
```

Debe responder 400 con un cuerpo `application/problem+json`. Sin cabecera
`Authorization`, espera 401.
