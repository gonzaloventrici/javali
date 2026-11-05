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
}