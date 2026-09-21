# Vocabulario vivo

Añadir conceptos cuando se utilicen y acompañarlos de un ejemplo comprobable.

| Término | Significado en este proyecto |
| --- | --- |
| Recurso / resource | Enlace guardado con título y motivo para estudiarlo. |
| Pendiente / pending | Estado inicial: todavía no se ha comenzado a trabajar el recurso. |
| Identidad / identity | Identificador estable que distingue un recurso de otro, aunque cambie su título. |
| Contrato HTTP / HTTP contract | Petición aceptada, respuesta y efectos prometidos por un endpoint. |
| Validación / validation | Comprobación de que una entrada cumple las reglas antes de aceptarla. |
| Criterio de aceptación / acceptance criterion | Comportamiento observable que permite decidir si una funcionalidad está terminada. |
| Migración / migration | Script versionado (Flyway) que define o cambia el esquema de la base. |
| Transacción / transaction | Unidad de trabajo en la base: se confirma completa o se deshace. |
| Repositorio / repository | Aquí: clase que ejecuta SQL concreto para guardar o leer recursos. |
| Constraint | Regla en la base (NOT NULL, CHECK, PK) que rechaza datos inválidos. |
| Autenticación / authentication | Demostrar identidad (aquí: API key Bearer válida). |
| Autorización / authorization | Decidir si ese principal puede operar sobre un recurso. |
| Ownership | El recurso guarda `owner_id`; solo ese dueño lo lee. |
| API key | Secreto opaco que el cliente envía; el servidor lo resuelve a un usuario. |
| Principal | Identidad autenticada disponible en el `SecurityContext` del request. |

Unicidad por propietario, idempotencia y concurrencia se desarrollarán en el siguiente hito.
