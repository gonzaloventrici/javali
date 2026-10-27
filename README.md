# Javali
 
Javali es una aplicación web para aprender Java con lecciones cortas y graduales, al estilo Duolingo. El estudiante avanza por unidades (variables, condicionales, bucles, clases y objetos) resolviendo ejercicios variados: completar código con opciones, ordenar líneas, encontrar el error y, más adelante, escribir código Java real.
 
## Problemática
 
Quienes empiezan con Java se frustran por los mensajes de error del compilador, difíciles de entender, y por conceptos abstractos como clases y objetos. Esto lleva a abandonar o a copiar código sin comprenderlo.
 
## Propósito
 
Que el estudiante aprenda a su ritmo y deje de tenerle miedo al error.
 
## Diseño Universal para el Aprendizaje (DUA)
 
- **Múltiples formas de representación:** cada concepto se presenta con código, diagramas y explicaciones simples.
- **Accesibilidad visual:** ajustes de tamaño de letra, contraste y tipografía.
- **Múltiples formas de acción:** el estudiante elige cómo practicar (opciones, ordenar o escribir) y puede usar todo con teclado.
- **Aprendizaje sin presión:** avanza a su ritmo, sin vidas ni penalizaciones por equivocarse.
- **Motivación opcional:** racha de práctica opcional, solo como motivador, sin rankings.
## Tipos de ejercicio
 
| Tipo | Descripción |
|---|---|
| Opciones | Completar código eligiendo entre varias opciones |
| Ordenar | Ordenar las líneas de un programa |
| Encontrar el error | Identificar y entender el error en un fragmento de código |
| Escribir código | Escribir código Java real (etapa posterior) |
 
## Tecnologías
 
- Java 21
- Spring Boot 4.1.2
- Maven
- Spring Web
- Validation
## Arquitectura
 
El proyecto se organiza en paquetes que separan responsabilidades:
 
```
com.javali
├── controllers    # Endpoints de la API
├── services       # Interfaces de la lógica de negocio
│   └── impl       # Implementaciones de los services
├── repositories   # Interfaces de acceso a datos
│   └── impl       # Implementaciones de los repositories
├── models         # Entidades del dominio
├── dtos           # Objetos de transferencia de datos
└── exceptions     # Excepciones propias del sistema
```
 
## Cómo ejecutarlo
 
Requisitos: JDK 21.
 
```bash
./mvnw spring-boot:run
```
 
En Windows (PowerShell):
 
```powershell
.\mvnw spring-boot:run
```
 
La aplicación queda disponible en `http://localhost:8080`.
 
## Estado del proyecto
 
En desarrollo. Trabajo práctico de la Licenciatura en Desarrollo de Software, UNSAM.