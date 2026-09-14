# Producto y alcance

## Uso

Guardar un recurso interesante para estudiarlo más adelante. En hitos posteriores,
registrar lo aprendido y descargar una nota Markdown revisable.

## Recurso

Un recurso tiene:

- `id`: UUID asignado por el servidor, estable durante su vida.
- `title`: título aportado por el usuario; entre 1 y 200 caracteres tras quitar espacios exteriores.
- `url`: URL absoluta HTTP o HTTPS, con host y sin credenciales; máximo 2048 caracteres.
- `reason`: motivo opcional para estudiarlo; máximo 1000 caracteres.
- `status`: inicialmente `PENDING`.
- `createdAt`: instante asignado por el servidor y expuesto en UTC.

La aplicación almacena el enlace, sin acceder a su contenido. La validación de
su formato no garantiza que el destino exista o sea accesible.

## Recorrido guardar → consultar

`POST /api/resources` acepta `title`, `url` y `reason`.

- Datos válidos: `201 Created`, recurso en JSON y cabecera `Location`.
- Datos inválidos: `400 Bad Request`, error identificable y ninguna escritura.

`GET /api/resources/{id}` devuelve:

- `200 OK` y los datos del recurso si existe.
- `404 Not Found` para un UUID válido inexistente.
- `400 Bad Request` si el identificador no tiene formato UUID.

## Persistencia

Los recursos se guardan en PostgreSQL. Reiniciar la aplicación no borra los datos
mientras la base siga disponible. El esquema lo aplica Flyway al arrancar.

## Criterios de aceptación

1. Guardar y consultar por el identificador devuelto conserva los datos y el estado inicial.
2. El título se guarda sin espacios exteriores.
3. Un título vacío o una URL inválida produce un error sin crear el recurso.
4. Dos recursos nuevos reciben identificadores distintos.
5. Consultar un identificador inexistente devuelve 404.
6. Tras reiniciar la aplicación (misma base), el GET por el id creado sigue devolviendo el recurso.

## Límite deliberado de este hito

Solo uso local, sin autenticación. Se permiten URLs repetidas temporalmente: la
regla de unicidad por propietario y sus pruebas de concurrencia se introducirán
junto con usuarios.

No incluye etiquetas, listados, cambios de estado, notas, exportación, jobs ni interfaz.
Estos pertenecen a hitos posteriores.
