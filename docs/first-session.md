# Sesión 1: guardar y consultar

## Objetivo

Poder explicar qué ocurre entre enviar un JSON y recuperar el recurso creado.

## Antes de implementar

Predecir y comentar:

1. ¿Quién asigna el identificador y por qué?
2. ¿Qué diferencia hay entre una entrada inválida y un recurso inexistente?
3. ¿Qué datos deberían seguir iguales al consultar el recurso guardado?

## Ejemplo del contrato

Petición a `POST /api/resources`:

```json
{
  "title": "Transacciones en PostgreSQL",
  "url": "https://www.postgresql.org/docs/current/tutorial-transactions.html",
  "reason": "Entender qué ocurre cuando falla una escritura"
}
```

El servidor responde 201 con un identificador, `PENDING`, la fecha de creación
y los datos guardados. `Location` indica dónde consultar el recurso.

## Primera prueba automatizada prevista

1. Arrancar la aplicación de pruebas con un puerto disponible.
2. Enviar la petición válida.
3. Verificar 201, identificador y Location.
4. Consultar Location.
5. Verificar 200 y los datos persistidos en memoria, incluidos id y createdAt.

Después se añaden casos de validación y recurso inexistente. Cada prueba tendrá
estado aislado y no dependerá del orden de ejecución.

## Cierre

- Aplicación ejecutable localmente y prueba del recorrido pasando.
- Miguel puede explicar controlador, validación y almacenamiento.
- README actualizado con los comandos efectivamente verificados.

## Estado de implementación — 2026-09-09

- Java y Maven alineados con JDK 21 para el build.
- Dependencias descargadas con autorización.
- Recorrido implementado y `mvn verify` completado: 12 pruebas, sin fallos ni errores.
- JAR ejecutado y recorrido POST 201 → GET 200 comprobado con curl.
- Servidor de comprobación detenido al terminar.
- Pendiente contigo: predicciones de [sesión 2](second-session.md) y recorrido
  manual Compose → POST → reinicio → GET.

La prueba usa HTTP real en un puerto aleatorio y un contexto nuevo por caso para
aislar el almacenamiento. No demuestra persistencia tras reinicio, seguridad,
capacidad bajo carga ni ausencia de fallos de concurrencia.
