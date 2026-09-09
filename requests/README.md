# Probar a mano

Con el servidor arrancado y la terminal situada en la raíz del proyecto:

```sh
curl -i http://127.0.0.1:8080/api/resources \
  -H 'Content-Type: application/json' \
  --data-binary @requests/create-resource.json
```

La respuesta incluye `201` y una cabecera `Location` con el path del recurso.
Copia ese path después de `http://127.0.0.1:8080` y consúltalo con `curl -i`.
En Bruno o Postman puedes usar el mismo JSON y método POST.

Para observar un error:

```sh
curl -i http://127.0.0.1:8080/api/resources/not-a-uuid
```

Debe responder 400 con un cuerpo `application/problem+json`.
