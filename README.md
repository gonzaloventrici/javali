# Javali

Javali es una aplicación web educativa para aprender Java mediante cursos, unidades, lecciones y ejercicios con retroalimentación específica. El proyecto fue desarrollado como Trabajo Práctico Integrador de Programación III en la Escuela de Ciencia y Tecnología de la Universidad Nacional de San Martín.

## Objetivo

Javali busca acompañar a estudiantes que comienzan a programar en Java y encuentran dificultades para comprender mensajes de error, reglas sintácticas y conceptos como clases, objetos, colecciones y excepciones.

El sistema permite:

- Realizar una evaluación diagnóstica inicial de 12 preguntas con código, de básicas a avanzadas.
- Obtener un nivel recomendado: `INICIAL`, `INTERMEDIO` o `AVANZADO`.
- Ver el avance por curso y unidad: completados, en curso y bloqueados.
- Elegir o modificar el nivel de aprendizaje.
- Recorrer cursos, unidades y lecciones ordenadas.
- Resolver ejercicios de tres tipos: elegir una opción, ordenar líneas de código y encontrar la línea con el error.
- Recibir explicaciones y pistas según el error cometido.
- Registrar intentos y progreso por lección.
- Consultar fortalezas, temas pendientes y recomendaciones.
- Ajustar tamaño de letra, contraste y tipografía, y usar toda la aplicación con teclado.
- Ver una racha de práctica opcional, sin vidas, penalizaciones ni rankings.

## Tecnologías utilizadas

- Java 21
- Spring Boot 4
- Spring MVC
- Spring Security con autenticación JWT para la API (OAuth2 Resource Server y Nimbus)
- Spring Data JPA
- Hibernate
- Jakarta Validation
- Thymeleaf
- Maven Wrapper
- **SQL Server** como base de datos por defecto
- H2 en memoria para el perfil `demo` y las pruebas de contexto
- JUnit 5, Mockito y MockMvc para las pruebas unitarias
- JaCoCo para la cobertura
- **pytest** y `requests` para las pruebas de la API por HTTP
- Postman para Visual Studio Code

## Arquitectura

El proyecto utiliza una arquitectura en capas:

```text
javali/
├── src/main/java/com/javali/
│   ├── config/              Configuración, seguridad (JWT y sesión) y datos demo
│   ├── controllers/         Endpoints REST (incluye AuthController)
│   │   └── web/             Navegación web y vistas Thymeleaf
│   ├── dtos/                Contratos de entrada y salida
│   ├── exceptions/          Excepciones del dominio
│   ├── models/              Entidades y enumeraciones
│   ├── repositories/        Acceso a datos con Spring Data JPA
│   │   └── impl/            Implementaciones personalizadas de repositorios
│   ├── security/            Usuarios para Spring Security y autorización por usuario
│   ├── services/            Interfaces de casos de uso
│   │   └── impl/            Implementación de reglas de negocio
│   ├── utils/               Funciones auxiliares sin estado (orden de líneas, racha)
│   ├── validators/          Reglas de validación propias
│   └── JavaliApplication.java
├── src/main/resources/
│   ├── templates/           Vistas Thymeleaf
│   ├── static/              Estilos (accesibilidad) y JavaScript de la interfaz
│   ├── application.properties
│   ├── application-sqlserver.properties
│   └── application-demo.properties
├── src/test/                Pruebas unitarias (JUnit, Mockito, MockMvc)
├── pytest_api/              Pruebas de la API REST por HTTP (pytest)
└── postman/                 Colecciones para cargar contenidos
```

### Responsabilidades principales

| Capa                | Responsabilidad                                                                                                    |
| ------------------- | ------------------------------------------------------------------------------------------------------------------ |
| `controllers`       | Recibir solicitudes REST, validar DTO y devolver respuestas HTTP.                                                  |
| `controllers.web`   | Atender navegación HTML, formularios, sesión, `Model` y plantillas Thymeleaf.                                      |
| `services`          | Declarar los contratos de los casos de uso.                                                                        |
| `services.impl`     | Aplicar reglas de negocio, transacciones, correcciones, cálculos y emisión de tokens JWT.                          |
| `repositories`      | Consultar y persistir información mediante Spring Data JPA.                                                        |
| `repositories.impl` | Consultas personalizadas: `IntentoRepositoryCustomImpl` obtiene solo los días con práctica para calcular la racha. |
| `models`            | Representar entidades, relaciones y estado del dominio.                                                            |
| `dtos`              | Transportar datos sin exponer directamente las entidades.                                                          |
| `config`            | Configurar seguridad, perfiles de ejecución y datos iniciales.                                                     |
| `security`          | Cargar usuarios para la autenticación y decidir si un usuario puede acceder a datos de otro.                       |
| `validators`        | `EjercicioValidator` valida cada tipo de ejercicio y sus respuestas; `@PasswordSegura` exige letras y números.     |
| `utils`             | `OrdenUtils` y `RachaUtils`: cálculos puros y fáciles de probar.                                                   |

## Requisitos

- Java 21
- Git
- SQL Server (por ejemplo, la edición Express) y SQL Server Management Studio, para el modo normal
- Python 3.11 o superior, solo para las pruebas con pytest
- Visual Studio Code y la extensión oficial de Postman, para cargar contenidos

No es necesario instalar Maven porque el repositorio incluye Maven Wrapper. Si no se quiere instalar SQL Server, se puede usar el perfil `demo` con H2.

## Clonar y abrir el proyecto

```powershell
git clone URL_DEL_REPOSITORIO
cd javali
code .
```

Comprobar que la terminal esté ubicada en la carpeta que contiene `pom.xml`:

```powershell
Test-Path .\pom.xml
```

Resultado esperado: `True`.

## Configurar SQL Server

1. En SQL Server Management Studio, conectarse con **Windows Authentication** y crear la base:

```sql
CREATE DATABASE javali;
```

2. Habilitar el inicio de sesión `sa` con una contraseña propia:

```sql
ALTER LOGIN sa ENABLE;
ALTER LOGIN sa WITH PASSWORD = 'UnaClave#Segura1';
```

3. Activar el modo mixto: clic derecho en el servidor → _Properties_ → _Security_ → **SQL Server and Windows Authentication mode**, y reiniciar el servicio.
4. Si la instancia es Express, habilitar **TCP/IP** con el puerto fijo `1433` desde _SQL Server Configuration Manager_ y reiniciar el servicio.

### Variables de entorno

La conexión se configura en `src/main/resources/application-sqlserver.properties` con variables de entorno. Solo `DB_PASSWORD` es obligatoria; el resto tiene un valor por defecto.

| Variable                 | Valor por defecto   | Descripción                                          |
| ------------------------ | ------------------- | ---------------------------------------------------- |
| `DB_PASSWORD`            | —                   | Contraseña del usuario de la base.                   |
| `DB_USER`                | `sa`                | Usuario de la base.                                  |
| `DB_NAME`                | `javali`            | Nombre de la base.                                   |
| `DB_HOST`                | `localhost`         | Servidor.                                            |
| `DB_PORT`                | `1433`              | Puerto.                                              |
| `JWT_SECRET`             | valor de desarrollo | Clave para firmar los tokens (mínimo 32 caracteres). |
| `JWT_EXPIRACION_MINUTOS` | `60`                | Duración del token.                                  |

En PowerShell, definir la contraseña en la misma ventana donde se inicia la aplicación:

```powershell
$env:DB_PASSWORD='tu_contraseña_real'
```

También se puede copiar `.env.example` a `.env` y completarlo. El archivo `.env` lo lee Visual Studio Code al iniciar con Run/Debug, pero no `mvnw`, y está en `.gitignore` para que las credenciales no se suban al repositorio.

## Ejecutar las pruebas unitarias

```powershell
.\mvnw.cmd clean test
```

No requieren SQL Server: el test de contexto usa el perfil `demo` con H2. Resultado esperado: `BUILD SUCCESS`.

El informe de JaCoCo se genera en:

```text
target/site/jacoco/index.html
```

## Iniciar la aplicación

Con SQL Server (modo por defecto):

```powershell
$env:DB_PASSWORD='tu_contraseña_real'
.\mvnw.cmd spring-boot:run
```

Sin SQL Server, con H2 en memoria:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=demo"
```

Esperar hasta ver `Started JavaliApplication`. La aplicación queda disponible en:

```text
http://localhost:8080
```

Hibernate crea las tablas en el primer arranque (`ddl-auto=update`). Con SQL Server los datos persisten entre reinicios; con el perfil `demo` se eliminan al detener la aplicación.

Documentación interactiva de la API (Swagger):

```text
http://localhost:8080/swagger-ui/index.html
```

## Usuarios de demostración

Se crean en el primer arranque, solo si todavía no existen.

| Rol           | Email                   | Contraseña      |
| ------------- | ----------------------- | --------------- |
| Administrador | `vebes@javali.com`      | `vebes123`      |
| Estudiante    | `estudiante@javali.com` | `Estudiante123` |

Las contraseñas se almacenan como hashes BCrypt. En una base nueva, el administrador queda con `id` 1 y el estudiante con `id` 2.

## Autenticación con JWT (API)

La API REST (`/api/**`) no usa sesión: cada pedido lleva un token JWT. Las pantallas web (`/login`, `/web/**`) siguen usando sesión con formulario.

1. Iniciar sesión para obtener el token:

```powershell
$r = Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/auth/login `
  -ContentType 'application/json' `
  -Body '{"email":"vebes@javali.com","password":"vebes123"}'
$r.token
```

La respuesta incluye `token`, `tipo` (`Bearer`), `expiraEnSegundos`, `usuarioId`, `email` y `rol`.

2. Enviar el token en los demás pedidos:

```powershell
Invoke-RestMethod -Uri http://localhost:8080/api/usuarios/1 `
  -Headers @{ Authorization = "Bearer $($r.token)" }
```

Detalles:

- El token se firma con HS256 e incluye el email del usuario y su rol (`ADMINISTRADOR` o `ESTUDIANTE`).
- Dura 60 minutos por defecto. Si vence, hay que iniciar sesión de nuevo.
- Sin token, o con un token inválido, la API responde `401`. Con un token válido pero sin permisos, responde `403`.
- La autenticación Basic ya no se acepta en la API.
- Para entregar o publicar el proyecto, definir `JWT_SECRET` con una clave propia de al menos 32 caracteres.

## Cargar los contenidos desde Postman

Las colecciones y environments se encuentran en:

```text
postman/
├── demo/
│   ├── Javali_Demo.postman_collection.json
│   └── Javali_Demo.postman_environment.json
└── datos-ampliados/
    ├── Javali_Datos_Ampliados.postman_collection.json
    ├── Javali_Datos_Ampliados.postman_environment.json
    └── README.md
```

> **Usar `datos-ampliados`.** Esa colección ya inicia sesión con JWT (`01 - Login (JWT)`) y guarda el token en el environment. La carpeta `demo` todavía usa autenticación Basic, que la API ya no acepta.

### Importar desde Visual Studio Code

1. Iniciar Javali.
2. Abrir la extensión Postman.
3. Seleccionar **Import** → **Folder**.
4. Elegir `postman/datos-ampliados`.
5. Seleccionar el environment `Javali Datos Ampliados`.
6. Ejecutar **Run folder** sobre `Carga ampliada completa`.
7. Abrir `http://localhost:8080/web/cursos` para ver el contenido.

La carga crea 3 cursos, 6 unidades, 12 lecciones y 24 ejercicios con su retroalimentación.

> Ejecutar la carga **una sola vez por base de datos**. Con SQL Server los datos persisten, así que repetirla duplica los contenidos.

## Pruebas de la API con pytest

La carpeta `pytest_api/` contiene 45 pruebas que verifican la API REST por HTTP contra la aplicación en ejecución: login con JWT, permisos por rol, cursos, contenido, respuestas y progreso. Complementan las pruebas JUnit de `src/test`.

Preparación (una sola vez):

```powershell
cd pytest_api
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
```

Ejecución, con Javali corriendo en otra terminal:

```powershell
pytest
```

Para correr un solo archivo: `pytest test_auth.py`. Por defecto usan `http://localhost:8080` y el administrador del demo; se pueden cambiar con las variables `JAVALI_BASE_URL`, `JAVALI_ADMIN_EMAIL`, `JAVALI_ADMIN_PASSWORD`, `JAVALI_ESTUDIANTE_EMAIL` y `JAVALI_ESTUDIANTE_PASSWORD`.

| Archivo                       | Qué verifica                                                                      |
| ----------------------------- | --------------------------------------------------------------------------------- |
| `test_auth.py`                | Login, tokens inválidos o adulterados, rechazo de Basic y registro.               |
| `test_cursos.py`              | Alta, consulta, modificación y baja de cursos, y permisos por rol.                |
| `test_contenido.py`           | Unidades, lecciones y ejercicios, validaciones y respuestas 404.                  |
| `test_respuestas_progreso.py` | Corrección de respuestas, retroalimentación, progreso, intentos y accesos ajenos. |

Con SQL Server los datos persisten, por lo que las pruebas dejan un mínimo de datos: el estudiante `pytest.estudiante@javali.com` y el curso inactivo `[pytest] Curso de pruebas`, que se crean una sola vez y se reutilizan. Todo lo demás se elimina al terminar.

## Recorrido recomendado

### Estudiante

```text
Registrarse o iniciar sesión
→ Evaluación inicial
→ Elegir nivel
→ Cursos
→ Unidades
→ Lecciones
→ Resolver ejercicio
→ Ver retroalimentación
→ Continuar al siguiente ejercicio
→ Mi progreso
```

Cuando una respuesta es incorrecta, el sistema puede informar la categoría del error, una explicación específica y una pista para resolverlo. Las categorías disponibles son `SINTAXIS`, `COMPILACION`, `LOGICA` y `CONCEPTO`.

### Administrador

El administrador puede utilizar la API para crear y administrar cursos, unidades, lecciones, ejercicios, retroalimentaciones y preguntas de evaluación.

## Evaluación inicial

El banco de preguntas está en `config/BancoPreguntasEvaluacion.java`: 4 básicas, 5 intermedias y 3 avanzadas, algunas con código y una sobre un mensaje real del compilador.

La evaluación diagnóstica:

1. Crea o recupera una evaluación pendiente.
2. Presenta preguntas de opción múltiple.
3. Exige responder todas las preguntas.
4. Calcula el puntaje y recomienda un nivel.
5. Permite confirmar o cambiar el nivel recomendado.

Reglas del puntaje:

- Cada pregunta tiene una dificultad de 1 a 3 que funciona como peso: las difíciles valen más.
- Menos de 40 % del puntaje recomienda `INICIAL` y menos de 70 %, `INTERMEDIO`.
- Para recomendar `AVANZADO` hace falta, además, resolver al menos la mitad de las preguntas avanzadas.
- El enlace **Evaluación inicial** del menú desaparece cuando el estudiante termina la evaluación y elige su nivel.

## Progreso

El progreso se registra por usuario y lección. La página **Mi progreso** muestra:

- Intentos realizados.
- Respuestas correctas.
- Porcentaje general.
- Error más frecuente.
- Recomendación, fortalezas y temas pendientes.
- Nivel recomendado y nivel elegido.
- Progreso de cada lección iniciada.

Las lecciones sin intentos no se muestran. Una lección iniciada aparece como **Lección en progreso** o **Lección completada**. Se considera completada cuando alcanza al menos un 80 % de respuestas correctas sobre los intentos registrados.

## Tipos de ejercicio

| Tipo              | Cómo se resuelve                                                  | Datos del ejercicio                                                                                |
| ----------------- | ----------------------------------------------------------------- | -------------------------------------------------------------------------------------------------- |
| `OPCIONES`        | Elegir una respuesta.                                             | `opciones` e `indiceCorrecto`.                                                                     |
| `ENCONTRAR_ERROR` | Elegir la línea de código que tiene el error.                     | `opciones` son las líneas e `indiceCorrecto` la que falla.                                         |
| `ORDENAR`         | Ordenar las líneas con botones Subir y Bajar o con Alt + flechas. | `opciones` son las líneas (en cualquier orden) y `ordenCorrecto` los índices en el orden correcto. |

La retroalimentación por opción (`/api/ejercicios/{id}/retroalimentaciones`) funciona en `OPCIONES` y `ENCONTRAR_ERROR`. Un orden incorrecto en `ORDENAR` se registra como error de `LOGICA`. La respuesta correcta nunca se envía al navegador.

Escribir código Java real no está implementado: requeriría compilar y ejecutar código del estudiante en un entorno aislado.

## Accesibilidad (DUA) y racha

Cada estudiante puede elegir en **Accesibilidad** (`/web/preferencias`):

- Tamaño de letra: normal, grande o muy grande.
- Contraste: normal o alto.
- Tipografía: predeterminada, legible o amigable con la dislexia (usa fuentes del sistema; si el equipo tiene OpenDyslexic, se aplica).
- Racha de práctica: se puede desactivar.

Los ajustes se guardan en la tabla `preferencias_usuario` y se aplican en todas las pantallas mediante atributos `data-*` en `<html>` y `static/css/javali.css`. Las pantallas incluyen enlace para saltar al contenido, foco visible, etiquetas para lectores de pantalla y se pueden usar solo con teclado.

La racha cuenta días seguidos con al menos un intento; sigue viva si se practicó hoy o ayer. No hay vidas, penalizaciones ni rankings.

## Ruta de aprendizaje

`AvanceService` calcula qué está completado, en curso o bloqueado:

- Una lección se abre al completar la anterior, y la primera lección de una unidad, al completar la unidad previa.
- Las unidades siguientes se ven en la lista, marcadas como **Bloqueada**, aunque todavía no se puedan abrir.
- Una unidad sin lecciones aparece como **Próximamente** y no cuenta para el curso.
- Un curso se muestra como **completado** cuando todas sus lecciones están completadas, y el listado muestra el avance con una barra de progreso.
- Abrir por la URL una lección bloqueada vuelve a la lista con un aviso. El administrador no tiene bloqueos.

## Seguridad

Spring Security usa dos cadenas de filtros:

| Cadena | Rutas                          | Mecanismo                                              |
| ------ | ------------------------------ | ------------------------------------------------------ |
| API    | `/api/**`                      | Sin sesión. Token JWT en `Authorization: Bearer`.      |
| Web    | `/login`, `/web/**` y el resto | Sesión HTTP con formulario de login y protección CSRF. |

Además:

- Se diferencian los roles `ADMINISTRADOR` y `ESTUDIANTE`, y las operaciones administrativas se restringen por rol.
- Un estudiante solo accede a sus propios intentos, progreso y respuestas (`@PreAuthorize`); el administrador accede a todos.
- Las contraseñas se codifican con BCrypt y deben tener letras y números.
- La clave de firma del JWT (`JWT_SECRET`) y la contraseña de la base se leen de variables de entorno.

La interfaz no constituye por sí sola un control de seguridad: los permisos se verifican en el servidor.

## Persistencia y perfiles

| Perfil                    | Base de datos | Uso                                                                               |
| ------------------------- | ------------- | --------------------------------------------------------------------------------- |
| `sqlserver` (por defecto) | SQL Server    | Uso normal. Los datos persisten.                                                  |
| `demo`                    | H2 en memoria | Presentación y pruebas sin instalar nada. Los datos se pierden al detener Javali. |

Para usar H2, iniciar con `-Dspring-boot.run.profiles=demo`. Los usuarios de demostración se cargan en ambos perfiles.

## API REST

La aplicación expone endpoints para autenticación, usuarios, cursos, unidades, lecciones, ejercicios, retroalimentaciones, evaluaciones, preguntas, intentos y progreso. Los endpoints utilizan DTO de entrada y salida, Bean Validation, `ResponseEntity`, códigos HTTP apropiados y manejo centralizado de errores.

| Respuesta | Cuándo                                                                              |
| --------- | ----------------------------------------------------------------------------------- |
| `400`     | Datos inválidos.                                                                    |
| `401`     | Falta el token, es inválido o está vencido; o credenciales incorrectas en el login. |
| `403`     | El usuario no tiene permiso para la operación.                                      |
| `404`     | El recurso no existe.                                                               |
| `409`     | Email repetido o recurso con datos relacionados.                                    |

## Estrategia de pruebas

- **JUnit 5 y Mockito**: reglas de negocio, aislando repositories y dependencias.
- **MockMvc**: controllers y validaciones.
- **JaCoCo**: cobertura.
- **pytest**: contrato de la API completa por HTTP, incluida la seguridad con JWT.
- **Postman**: carga de contenidos y demostración.

Se priorizan la corrección de ejercicios, la clasificación de errores, la actualización del progreso, el desbloqueo de lecciones, la finalización de evaluaciones, el cálculo del nivel recomendado y los accesos anónimos, de estudiante y de administrador.

## Reiniciar la demostración

Con SQL Server, para volver a un estado limpio:

1. Detener Javali con `Ctrl + C`.
2. En SQL Server Management Studio, recrear la base:

```sql
USE master;
ALTER DATABASE javali SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
DROP DATABASE javali;
CREATE DATABASE javali;
```

3. Iniciar Javali de nuevo. Se crean las tablas y los usuarios de demostración.
4. Ejecutar otra vez la carga de `postman/datos-ampliados`.

Con el perfil `demo` alcanza con detener y volver a iniciar Javali.

## Problemas frecuentes

| Síntoma                                            | Causa y solución                                                                                                               |
| -------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------ |
| `Login failed for user 'sa'`                       | `DB_PASSWORD` no está definida en esa terminal o es incorrecta, o `sa` está deshabilitado o el servidor no está en modo mixto. |
| `Unable to determine Dialect`                      | Hibernate no pudo conectarse. Buscar más arriba en el log el error real de conexión.                                           |
| `The TCP/IP connection to the host ... has failed` | TCP/IP no está habilitado en el puerto 1433. Ver _Configurar SQL Server_.                                                      |
| La API responde `401` con un usuario correcto      | El token venció o falta el encabezado `Authorization: Bearer ...`.                                                             |
| pytest indica que Javali no responde               | La aplicación no está iniciada en `localhost:8080`.                                                                            |

## Documentación adicional

El repositorio puede incluir:

```text
README_PRESENTACION.md
01_Guia_de_estudio_Javali.docx
02_Informe_TPI_Javali.docx
```

- `README_PRESENTACION.md`: secuencia rápida para la exposición.
- `01_Guia_de_estudio_Javali.docx`: tecnologías, arquitectura y preparación de defensa.
- `02_Informe_TPI_Javali.docx`: informe técnico entregable.

## Estado del proyecto

Funcionalidades principales disponibles:

- Arquitectura en capas.
- API REST con autenticación JWT.
- Interfaz web Thymeleaf con sesión.
- Persistencia en SQL Server.
- Registro, roles y seguridad.
- Gestión de contenidos.
- Evaluación inicial, recomendación y elección de nivel.
- Corrección y retroalimentación.
- Ejercicios de opciones, de ordenar líneas y de encontrar el error.
- Ajustes de accesibilidad por usuario y racha opcional.
- Intentos y progreso por lección.
- Datos demo reproducibles.
- Pruebas unitarias con cobertura y pruebas de la API con pytest.

## Equipo

```text
Equipo: vebes
Integrantes: Bedacarratz Gonzalo, Bedacarratz Nicolas y Ventrici Gonzalo
Asignatura: Programación III
Institución: Escuela de Ciencia y Tecnología - UNSAM
```
