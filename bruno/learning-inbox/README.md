# Bruno — Learning Inbox

Colección HTTP versionada para pruebas manuales del API.

## Abrir en Bruno

1. `docker compose up -d` y `mvn spring-boot:run` (JDK 21).
2. En Bruno: **Open Collection** → elige esta carpeta
   (`bruno/learning-inbox`, la que contiene `bruno.json`).
3. Selecciona el entorno **local** (`apiKey` = Alice por defecto).

## Slice actual (hito 3)

1. Ejecuta **resources → Create resource** (`201`, Bearer).
   El script guarda `resourceId` como variable de runtime.
2. Ejecuta **resources → Get resource by id** (`200`).

Para ver aislamiento: cambia temporalmente `apiKey` a `li_bob_dev_key_002` y
repite el GET → `404`; vuelve a la key de Alice.

Para el smoke de persistencia: crea el recurso, para la app (Ctrl+C),
vuelve a arrancarla y repite el GET con el mismo `resourceId`.

Los bodies de ejemplo para `curl` siguen en [`requests/`](../../requests/).
