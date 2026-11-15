package com.javali.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ManejadorGlobalExcepciones {

    @ExceptionHandler(UnidadNoEncontradaException.class)
    public ProblemDetail manejarUnidadNoEncontrada(
            UnidadNoEncontradaException exception) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
        problema.setTitle("Unidad no encontrada");
        return problema;
    }
    // Convierte una unidad inexistente en una respuesta 404.

    @ExceptionHandler(LeccionNoEncontradaException.class)
    public ProblemDetail manejarLeccionNoEncontrada(
            LeccionNoEncontradaException exception) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
        problema.setTitle("Lección no encontrada");
        return problema;
    }
    // Convierte una lección inexistente en una respuesta 404.

    @ExceptionHandler(EjercicioNoEncontradoException.class)
    public ProblemDetail manejarEjercicioNoEncontrado(
            EjercicioNoEncontradoException exception) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
        problema.setTitle("Ejercicio no encontrado");
        return problema;
    }
    // Convierte un ejercicio inexistente en una respuesta 404.
    
    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ProblemDetail manejarUsuarioNoEncontrado(
                UsuarioNoEncontradoException exception) {

        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );

        problema.setTitle("Usuario no encontrado");

        return problema;
        }

        // Convierte un usuario inexistente en una respuesta 404.

    @ExceptionHandler(EmailYaRegistradoException.class)
    public ProblemDetail manejarEmailYaRegistrado(
                EmailYaRegistradoException exception) {

        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );

        problema.setTitle("Email ya registrado");

        return problema;
        }
        // Convierte un email repetido en una respuesta 409.

    @ExceptionHandler(CursoNoEncontradoException.class)
    public ProblemDetail manejarCursoNoEncontrado(
                CursoNoEncontradoException exception) {

        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );

        problema.setTitle("Curso no encontrado");

        return problema;
        }
        // Convierte un curso inexistente en una respuesta 404.

        @ExceptionHandler(EvaluacionNoEncontradaException.class)
        public ProblemDetail manejarEvaluacionNoEncontrada(
                EvaluacionNoEncontradaException exception) {

        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
        problema.setTitle("Evaluación no encontrada");
        return problema;
        }
        // Convierte una evaluación inexistente en una respuesta 404.

        @ExceptionHandler(PreguntaNoEncontradaException.class)
        public ProblemDetail manejarPreguntaNoEncontrada(
                PreguntaNoEncontradaException exception) {

        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
        problema.setTitle("Pregunta no encontrada");
        return problema;
        }
        // Convierte una pregunta inexistente en una respuesta 404.

        @ExceptionHandler(RespuestaEvaluacionInvalidaException.class)
        public ProblemDetail manejarRespuestaEvaluacionInvalida(
                RespuestaEvaluacionInvalidaException exception) {

        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );
        problema.setTitle("Respuesta de evaluación inválida");
        return problema;
        }
        // Convierte una evaluación inválida en una respuesta 400.

        @ExceptionHandler(RecursoConDependenciasException.class)
        public ProblemDetail manejarRecursoConDependencias(
                RecursoConDependenciasException exception) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
        problema.setTitle("El recurso contiene datos relacionados");
        return problema;
        }
        // Devuelve 409 cuando una relación impide eliminar el recurso.
}