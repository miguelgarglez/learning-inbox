# Trabajo en Learning Inbox

## Propósito

Miguel está aprendiendo Java y backend. La comprensión del sistema es parte del
resultado. Leer README.md, docs/product.md y el ejercicio activo antes de editar.

## Forma de trabajar

- Entregar una pequeña funcionalidad completa cada vez.
- Explicar brevemente el problema, la decisión y cómo verificarla.
- En la primera aparición de un concepto, invitar a Miguel a predecir el resultado
  y participar en la implementación. No resolver hitos posteriores por adelantado.
- Mantener el vocabulario del producto consistente en código, API y documentación.
- Registrar decisiones cuando exista un tradeoff real; evitar documentación ceremonial.
- No añadir frameworks, capas o servicios sin una necesidad del hito actual.
- Pedir confirmación antes de instalar o descargar dependencias, paquetes o programas.
- No ejecutar pruebas de carga contra servicios externos sin autorización específica.
- Usar datos ficticios. No incorporar contexto privado de la KB al repositorio.

## Aprendizaje y rigor (norma del proyecto)

La comprensión del sistema es parte del resultado, no un extra. En dudas, revisiones
de hito y primeras apariciones de conceptos, el agente debe:

- Anclar explicaciones en el código del repo, no en teoría genérica desconectada.
- Usar y corregir términos técnicos precisos (HTTP, DTO, bean, DI, validación,
  Problem Details, etc.); cuando haya jerga imprecisa, ofrecer el término correcto.
- Separar claramente: código propio vs lo que Spring Boot hace por anotaciones,
  starters o auto-configuración vs reglas de dominio escritas a mano.
- Invitar a predecir o narrar el flujo antes de cerrar la explicación del concepto.
- Tras introducir algo nuevo (p. ej. persistencia), aclarar qué es automático y qué no.

Detalle operativo en `.cursor/rules/learning-depth.mdc` (siempre activa en Cursor).

## Verificación

- Probar comportamientos y propiedades, no replicar detalles de implementación.
- Para persistencia, usar PostgreSQL aislado y desechable cuando esté disponible.
- Las pruebas de concurrencia deben buscar solapamiento y comprobar el estado final.
- Registrar entorno, carga y límites al comunicar resultados de rendimiento.
- Distinguir siempre pruebas ejecutadas, pendientes y limitaciones.

## Comandos

Usar JDK 21; ver docs/java-tooling.md para seleccionarlo.

- `mvn verify`: check principal, pruebas y empaquetado; verificado.
- `mvn spring-boot:run`: arranque de desarrollo.
- `java -jar target/learning-inbox-0.0.1-SNAPSHOT.jar`: arranque del artefacto, verificado.

El build descarga dependencias y escribe en ~/.m2/repository. Las pruebas HTTP
abren puertos locales; pueden necesitar permisos adicionales en un sandbox.
