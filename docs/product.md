# Producto y alcance

## Uso

Guardar un recurso interesante para estudiarlo más adelante. En hitos posteriores,
registrar lo aprendido y descargar una nota Markdown revisable.

## Recurso

Un recurso tiene:

- `id`: UUID asignado por el servidor, estable durante su vida.
- `ownerId`: UUID del usuario autenticado que lo creó; asignado por el servidor.
- `title`: título aportado por el usuario; entre 1 y 200 caracteres tras quitar espacios exteriores.
- `url`: URL absoluta HTTP o HTTPS, con host y sin credenciales; máximo 2048 caracteres.
- `reason`: motivo opcional para estudiarlo; máximo 1000 caracteres.
- `status`: inicialmente `PENDING`.
- `createdAt`: instante asignado por el servidor y expuesto en UTC.

La aplicación almacena el enlace, sin acceder a su contenido. La validación de
su formato no garantiza que el destino exista o sea accesible.

## Autenticación

Las rutas `/api/**` exigen la cabecera `Authorization: Bearer <api-key>`.

- Key válida: el servidor resuelve el usuario y lo usa como dueño de las operaciones.
- Ausente o desconocida: `401 Unauthorized`, sin escribir ni leer recursos.
- En local/tests hay seeds `alice` / `bob` (`li_alice_dev_key_001` /
  `li_bob_dev_key_002`). Las keys van en claro en la base solo para aprendizaje.

## Recorrido guardar → consultar

`POST /api/resources` acepta `title`, `url` y `reason` (no acepta `ownerId`).

- Datos válidos y autenticados: `201 Created`, recurso en JSON y cabecera `Location`.
- Datos inválidos: `400 Bad Request`, error identificable y ninguna escritura.
- Sin autenticación válida: `401`.

`GET /api/resources/{id}` devuelve:

- `200 OK` y los datos si el recurso existe **y** pertenece al usuario autenticado.
- `404 Not Found` para un UUID válido inexistente **o** de otro usuario.
- `400 Bad Request` si el identificador no tiene formato UUID.
- `401` sin autenticación válida.

## Persistencia

Los recursos se guardan en PostgreSQL con `owner_id`. Reiniciar la aplicación no
borra los datos mientras la base siga disponible. El esquema lo aplica Flyway
(`V1` recursos, `V2` usuarios, keys y ownership).

## Criterios de aceptación

1. Guardar y consultar con la misma key conserva los datos, el dueño y el estado inicial.
2. El título se guarda sin espacios exteriores.
3. Un título vacío o una URL inválida produce un error sin crear el recurso.
4. Dos recursos nuevos reciben identificadores distintos.
5. Consultar un identificador inexistente o de otro usuario devuelve 404.
6. Tras reiniciar la aplicación (misma base), el GET autenticado por el id creado sigue devolviendo el recurso.
7. Sin key o con key inválida, POST/GET responden 401.

## Límite deliberado de este hito

Uso local con API keys en claro. Se permiten URLs repetidas por el mismo dueño
temporalmente: la unicidad por propietario y sus pruebas de concurrencia van en
un hito posterior.

No incluye registro de usuarios, hashing de keys, JWT/OAuth, etiquetas, listados,
cambios de estado, notas, exportación, jobs ni interfaz.
