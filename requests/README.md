# Probar a mano

Con Docker Compose arriba (`docker compose up -d`) y el servidor arrancado,
desde la raíz del proyecto:

```sh
curl -i http://127.0.0.1:8080/api/resources \
  -H 'Content-Type: application/json' \
  --data-binary @requests/create-resource.json
```

La respuesta incluye `201` y una cabecera `Location` con el path del recurso.
Copia ese path después de `http://127.0.0.1:8080` y consúltalo con `curl -i`.
Puedes detener la app, volver a arrancarla y repetir el GET: el recurso sigue
en PostgreSQL.

## Bruno

Colección versionada en [`bruno/learning-inbox/`](../bruno/learning-inbox/):
abre esa carpeta en Bruno, entorno **local**, ejecuta Create y luego Get.
Detalle en el README de la colección.

Para observar un error:

```sh
curl -i http://127.0.0.1:8080/api/resources/not-a-uuid
```

Debe responder 400 con un cuerpo `application/problem+json`.
