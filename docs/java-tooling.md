# Java y Maven: comandos para trabajar en este proyecto

## Preparar una terminal en macOS

```sh
cd learning-inbox
export JAVA_HOME="$(/usr/libexec/java_home -v 21)"
export PATH="$JAVA_HOME/bin:$PATH"
java -version
mvn -version
```

Las dos últimas salidas deben indicar Java 21. `JAVA_HOME` selecciona el JDK para
Maven y `PATH` selecciona los ejecutables `java` y `javac` de esa misma instalación.
Estos cambios afectan a esa terminal, no editan tu configuración global.

## Las piezas

- **JDK:** herramientas de desarrollo, incluido el compilador `javac`, y entorno de ejecución.
- **JVM:** máquina virtual que ejecuta el bytecode compilado.
- **Maven:** resuelve dependencias, compila, ejecuta pruebas y empaqueta.
- **Spring Boot:** configura y arranca nuestra aplicación Spring y su servidor HTTP integrado.
- **JAR:** archivo que agrupa clases y recursos. Nuestro JAR ejecutable también incluye dependencias.

## Descargar dependencias

Se declaran en `pom.xml`. Maven descarga las que necesita automáticamente al compilar
o probar. No es necesario un comando de instalación previo.

```sh
mvn verify
```

Este es el comando principal de verificación del repo. Descarga lo necesario,
compila, ejecuta las pruebas actuales y genera el JAR en `target/`.

Para precargar dependencias y plugins, sin ejecutar las pruebas:

```sh
mvn dependency:go-offline
```

La caché local está normalmente en `~/.m2/repository`. Se comparte entre proyectos.
Los archivos generados de este proyecto van a `target/`, que no se versiona.
Una primera ejecución tarda más por las descargas.

## Comandos cotidianos

| Comando | Qué hace |
| --- | --- |
| `mvn compile` | Compila el código de aplicación. |
| `mvn test` | Compila y ejecuta las pruebas configuradas en la fase test. |
| `mvn -Dtest=ResourceApiTest test` | Ejecuta esta clase de pruebas. |
| `mvn package` | Llega hasta generar el JAR; también ejecuta las pruebas anteriores. |
| `mvn verify` | Llega hasta la fase de verificación; es nuestro check principal. |
| `mvn spring-boot:run` | Arranca la aplicación en desarrollo. Ctrl+C la detiene. |
| `mvn dependency:tree` | Muestra dependencias directas y transitivas. |
| `mvn clean` | Elimina `target/`; no borra la caché de dependencias. |

Las fases se encadenan: `compile → test → package → verify → install`.
No necesitas ejecutar cada una por separado. Los nombres con dos puntos, como
`spring-boot:run`, invocan un objetivo de un plugin.

**`mvn install` no equivale a `npm install`.** Ejecuta las fases anteriores y
copia el artefacto de TU proyecto al repositorio Maven local para que otros
proyectos puedan consumirlo. No lo necesitamos para arrancar esta API.

`verify` solo ejecuta las comprobaciones configuradas: no inventa pruebas de carga
o integración. Actualmente ResourceApiTest prueba HTTP real con JUnit y se ejecuta
mediante Surefire en la fase `test`. No hemos configurado Failsafe ni pruebas de carga.

## Dependencias de este proyecto

El parent fija Spring Boot 4.1.1 y gestiona un conjunto compatible de versiones.
No repetir versiones para dependencias gestionadas por el parent.

- `spring-boot-starter-webmvc`: MVC, serialización JSON y servidor HTTP integrado.
- `spring-boot-starter-validation`: validación de entradas mediante anotaciones Jakarta.
- `spring-boot-starter-jdbc`: `DataSource`, pool y `JdbcClient`.
- `spring-boot-starter-flyway` + `flyway-database-postgresql`: migraciones al arrancar.
- `postgresql`: driver JDBC (runtime).
- `spring-boot-starter-test`: JUnit y utilidades de prueba; `scope=test`.
- `spring-boot-testcontainers` + módulos Testcontainers: Postgres desechable en tests.

Un starter agrupa dependencias para un caso de uso. Dependencia transitiva significa
que llega porque otra dependencia la necesita.

## PostgreSQL local con Docker Compose

```sh
docker compose up -d
docker compose ps
docker compose down
```

La app lee `spring.datasource.*` en `application.properties` (usuario/clave/db
`learning_inbox` en `127.0.0.1:5432`). `mvn verify` no usa Compose: Testcontainers
levanta su propio Postgres.

## Ejecutar el artefacto empaquetado

```sh
java -jar target/learning-inbox-0.0.1-SNAPSHOT.jar
```

Necesita haber ejecutado `mvn package` o `mvn verify`. El JAR arranca con Java;
no necesita Maven para ejecutarse. Esta distinción separa construir de desplegar.

El servidor escucha en `127.0.0.1:8080`. En este hito no hay autenticación. Con
Compose arriba, los datos viven en PostgreSQL y sobreviven a reiniciar la JVM.
Un GET a `/` devuelve 404: no existe una página web.

## Fuentes

- [Ciclo de vida de Maven](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html)
- [Precarga de dependencias](https://maven.apache.org/plugins/maven-dependency-plugin/go-offline-mojo.html)
- [Plugin Maven de Spring Boot](https://docs.spring.io/spring-boot/maven-plugin/using.html)
