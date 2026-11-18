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
- Spring Boot
- Spring MVC
- Spring Security
- Spring Data JPA
- Hibernate
- Jakarta Validation
- Thymeleaf
- Maven Wrapper
- SQL Server para el perfil local
- H2 en memoria para el perfil demo
- JUnit 5
- Mockito
- MockMvc
- JaCoCo
- Postman para Visual Studio Code

## Arquitectura

El proyecto utiliza una arquitectura en capas:

```text
src/main/java/com/javali/
├── config/              Configuración, seguridad y datos demo
├── controllers/         Endpoints REST
│   └── web/             Navegación web y vistas Thymeleaf
├── dtos/                Contratos de entrada y salida
├── exceptions/          Excepciones del dominio
├── models/              Entidades y enumeraciones
├── repositories/        Acceso a datos con Spring Data JPA
│   └── impl/            Implementaciones personalizadas de repositorios
├── services/            Interfaces de casos de uso
│   └── impl/            Implementación de reglas de negocio
├── utils/               Funciones auxiliares sin estado (orden de líneas, racha)
├── validators/          Reglas de validación propias
└── JavaliApplication.java

src/main/resources/
├── templates/           Vistas Thymeleaf
└── static/
    ├── css/javali.css   Hoja de estilos común con los ajustes de accesibilidad
    └── js/javali.js     Reordenar líneas con teclado y foco en el resultado
```

### Responsabilidades principales

| Capa | Responsabilidad |
|---|---|
| `controllers` | Recibir solicitudes REST, validar DTO y devolver respuestas HTTP. |
| `controllers.web` | Atender navegación HTML, formularios, sesión, `Model` y plantillas Thymeleaf. |
| `services` | Declarar los contratos de los casos de uso. |
| `services.impl` | Aplicar reglas de negocio, transacciones, correcciones y cálculos. |
| `repositories` | Consultar y persistir información mediante Spring Data JPA. |
| `repositories.impl` | Consultas personalizadas: `IntentoRepositoryCustomImpl` obtiene solo los días con práctica para calcular la racha. |
| `models` | Representar entidades, relaciones y estado del dominio. |
| `dtos` | Transportar datos sin exponer directamente las entidades. |
| `config` | Configurar seguridad, perfiles de ejecución y datos iniciales. |
| `validators` | `EjercicioValidator` valida cada tipo de ejercicio y sus respuestas; `@PasswordSegura` exige letras y números. |
| `utils` | `OrdenUtils` y `RachaUtils`: cálculos puros y fáciles de probar. |

## Requisitos

Para ejecutar el perfil de demostración se necesita:

- Java 21
- Git
- Visual Studio Code
- Extensión oficial de Postman para Visual Studio Code

No es necesario instalar Maven porque el repositorio incluye Maven Wrapper.

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

Resultado esperado:

```text
True
```

## Ejecutar las pruebas

```powershell
.\mvnw.cmd clean test
```

Resultado esperado:

```text
BUILD SUCCESS
```

El informe de JaCoCo se genera en:

```text
target/site/jacoco/index.html
```

## Iniciar la aplicación en modo demo

```powershell
.\mvnw.cmd spring-boot:run
```

Esperar hasta ver:

```text
Started JavaliApplication
```

La aplicación queda disponible en:

```text
http://localhost:8080
```

El perfil demo utiliza una base H2 en memoria:

```text
jdbc:h2:mem:javali
```

Los datos se eliminan al detener la aplicación.

## Usuarios de demostración

### Administrador

```text
Email: vebes@javali.com
Contraseña: vebes123
```

### Estudiante

```text
Email: estudiante@javali.com
Contraseña: Estudiante123
```

Las contraseñas se almacenan como hashes BCrypt.

## Cargar los contenidos desde Postman

Las colecciones y environments se encuentran en:

```text
postman/
```

La estructura puede incluir:

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

### Importar desde Visual Studio Code

1. Iniciar Javali.
2. Abrir la extensión Postman.
3. Seleccionar **Import**.
4. Elegir **Folder**.
5. Seleccionar `postman/datos-ampliados` o `postman/demo`.
6. Importar la colección y el environment.
7. Seleccionar el environment correspondiente.
8. Ejecutar primero `00 - Verificar API` o `01 - Verificar API`.
9. Confirmar que la respuesta sea `200 OK`.
10. Ejecutar **Run folder** sobre la carpeta de carga.

> Ejecutar una sola colección y una sola vez por inicio de H2. Si se ejecuta nuevamente sin reiniciar, se pueden duplicar los contenidos.

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

Cuando una respuesta es incorrecta, el sistema puede informar:

- Categoría del error.
- Explicación específica.
- Pista para resolverlo.

Las categorías disponibles son:

```text
SINTAXIS
COMPILACION
LOGICA
CONCEPTO
```

### Administrador

El administrador puede utilizar la API para crear y administrar:

- Cursos.
- Unidades.
- Lecciones.
- Ejercicios.
- Retroalimentaciones.
- Preguntas de evaluación.

## Evaluación inicial

La evaluación diagnóstica:

1. Crea o recupera una evaluación pendiente.
2. Presenta preguntas de opción múltiple.
3. Exige responder todas las preguntas.
4. Calcula el porcentaje de respuestas correctas.
5. Recomienda un nivel.
6. Permite confirmar o cambiar el nivel recomendado.

Regla actual:

| Porcentaje | Nivel recomendado |
|---:|---|
| Menos de 40% | `INICIAL` |
| Desde 40% y menos de 70% | `INTERMEDIO` |
| Desde 70% | `AVANZADO` |

## Progreso

El progreso se registra por usuario y lección.

La página **Mi progreso** muestra:

- Intentos realizados.
- Respuestas correctas.
- Porcentaje general.
- Error más frecuente.
- Recomendación.
- Fortalezas.
- Temas pendientes.
- Nivel recomendado y nivel elegido.
- Progreso de cada lección iniciada.

Las lecciones sin intentos no se muestran. Una lección iniciada aparece como:

```text
Lección en progreso
```

o:

```text
Lección completada
```

La regla actual considera completada una lección cuando alcanza al menos un 80% de respuestas correctas sobre los intentos registrados.

## Tipos de ejercicio

| Tipo | Cómo se resuelve | Datos del ejercicio |
|---|---|---|
| `OPCIONES` | Elegir una respuesta. | `opciones` e `indiceCorrecto`. |
| `ENCONTRAR_ERROR` | Elegir la línea de código que tiene el error. | `opciones` son las líneas e `indiceCorrecto` la que falla. |
| `ORDENAR` | Ordenar las líneas con botones Subir y Bajar o con Alt + flechas. | `opciones` son las líneas (en cualquier orden) y `ordenCorrecto` los índices en el orden correcto. |

La retroalimentación por opción (`/api/ejercicios/{id}/retroalimentaciones`) funciona en `OPCIONES` y `ENCONTRAR_ERROR`. Un orden incorrecto en `ORDENAR` se registra como error de `LOGICA`. La respuesta correcta nunca se envía al navegador.

Escribir código Java real no está implementado: requeriría compilar y ejecutar código del estudiante en un entorno aislado.

## Accesibilidad (DUA) y racha

Cada estudiante puede elegir en **Accesibilidad** (`/web/preferencias`):

- Tamaño de letra: normal, grande o muy grande.
- Contraste: normal o alto.
- Tipografía: predeterminada, legible o amigable con la dislexia (usa fuentes del sistema; si el equipo tiene OpenDyslexic, se aplica).
- Racha de práctica: se puede desactivar.

Los ajustes se guardan en la tabla `preferencias_usuario` y se aplican en todas las pantallas mediante atributos `data-*` en `<html>` y `static/css/javali.css`. Las pantallas incluyen enlace para saltar al contenido, foco visible, etiquetas para lectores de pantalla y se pueden usar solo con teclado. Para quien no inició sesión se usan los valores predeterminados.

La racha cuenta días seguidos con al menos un intento; sigue viva si se practicó hoy o ayer. No hay vidas, penalizaciones ni rankings.

## Evaluación inicial

El banco de preguntas está en `config/BancoPreguntasEvaluacion.java`: 4 básicas, 5 intermedias y 3 avanzadas, algunas con código y una sobre un mensaje real del compilador.

- Cada pregunta tiene una dificultad de 1 a 3 que funciona como peso en el puntaje: las difíciles valen más.
- Menos de 40 % del puntaje recomienda `INICIAL` y menos de 70 %, `INTERMEDIO`.
- Para recomendar `AVANZADO` hace falta, además, resolver al menos la mitad de las preguntas avanzadas.
- El enlace **Evaluación inicial** del menú desaparece cuando el estudiante termina la evaluación y elige su nivel.

## Ruta de aprendizaje

`AvanceService` calcula qué está completado, en curso o bloqueado:

- Una lección se abre al completar la anterior, y la primera lección de una unidad, al completar la unidad previa.
- Las unidades siguientes se ven en la lista, marcadas como **Bloqueada**, aunque todavía no se puedan abrir.
- Una unidad sin lecciones aparece como **Próximamente** y no cuenta para el curso.
- Un curso se muestra como **completado** cuando todas sus lecciones están completadas, y el listado muestra el avance con una barra de progreso.
- Abrir por la URL una lección bloqueada vuelve a la lista con un aviso. El administrador no tiene bloqueos.

## Seguridad

El proyecto utiliza Spring Security para:

- Autenticar usuarios mediante sesión HTTP.
- Proteger rutas y recursos.
- Diferenciar roles `ADMINISTRADOR` y `ESTUDIANTE`.
- Restringir operaciones administrativas.
- Evitar que un usuario modifique evaluaciones ajenas.
- Codificar contraseñas con BCrypt y exigir contraseñas con letras y números.
- Proteger los formularios web con CSRF (la API REST `/api/**` queda excluida para poder probarla con Postman).

La interfaz no constituye por sí sola un control de seguridad. Los permisos se verifican en el servidor.

## Persistencia y perfiles

### Perfil demo

- Utiliza H2 en memoria.
- No requiere instalar una base de datos.
- Es adecuado para presentación y pruebas manuales.
- Pierde sus datos al detener Javali.

### Perfil local

Para utilizar SQL Server:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

La configuración privada debe mantenerse en:

```text
src/main/resources/application-local.properties
```

Ese archivo no debe versionarse si contiene credenciales reales.

## API REST

La aplicación expone endpoints para:

- Usuarios.
- Cursos.
- Unidades.
- Lecciones.
- Ejercicios.
- Retroalimentaciones.
- Evaluaciones.
- Preguntas.
- Intentos y progreso.

Los endpoints utilizan:

- DTO de entrada y salida.
- Bean Validation.
- `ResponseEntity`.
- Códigos HTTP apropiados.
- Manejo centralizado de errores.

## Pruebas

La estrategia de pruebas incluye:

- JUnit 5 para reglas de negocio.
- Mockito para aislar repositories y dependencias.
- Fixtures para construir escenarios reproducibles.
- MockMvc para controllers, validaciones y seguridad.
- JaCoCo para medir cobertura.
- Postman para pruebas manuales y demostración.

Se priorizan los siguientes casos:

- Corrección de ejercicios.
- Clasificación de errores.
- Actualización del progreso.
- Desbloqueo de lecciones.
- Finalización de evaluaciones.
- Cálculo del nivel recomendado.
- Accesos anónimos, de estudiante y de administrador.

## Reiniciar la demostración

1. Detener Javali:

```text
Ctrl + C
```

2. Iniciar nuevamente:

```powershell
.\mvnw.cmd spring-boot:run
```

3. Seleccionar el environment de Postman.
4. Ejecutar nuevamente una única carpeta de carga.

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
- API REST.
- Interfaz web Thymeleaf.
- Registro y autenticación.
- Roles y seguridad.
- Gestión de contenidos.
- Evaluación inicial.
- Recomendación y elección de nivel.
- Corrección y retroalimentación.
- Navegación al siguiente ejercicio.
- Ejercicios de ordenar líneas y de encontrar el error.
- Ajustes de accesibilidad por usuario y racha opcional.
- Intentos y progreso por lección.
- Datos demo reproducibles.
- Pruebas automatizadas y cobertura.

## Equipo

```text
Equipo: vebes
Asignatura: Programación III
Institución: Escuela de Ciencia y Tecnología - UNSAM
```
