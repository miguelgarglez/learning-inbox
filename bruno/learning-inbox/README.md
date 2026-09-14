# Bruno — Learning Inbox

Colección HTTP versionada para pruebas manuales del API.

## Abrir en Bruno

1. `docker compose up -d` y `mvn spring-boot:run` (JDK 21).
2. En Bruno: **Open Collection** → elige esta carpeta
   (`bruno/learning-inbox`, la que contiene `bruno.json`).
3. Selecciona el entorno **local**.

## Slice actual (hito 2)

1. Ejecuta **resources → Create resource** (`201`).
   El script guarda `resourceId` como variable de runtime.
2. Ejecuta **resources → Get resource by id** (`200`).

Para el smoke de persistencia: crea el recurso, para la app (Ctrl+C),
vuelve a arrancarla y repite el GET con el mismo `resourceId`
(sigue visible en el icono del ojo de variables de Bruno mientras no cierres la app).

Los bodies de ejemplo para `curl` siguen en [`requests/`](../../requests/).
