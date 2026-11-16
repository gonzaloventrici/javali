package com.javali.exceptions;

public class RespuestaEvaluacionInvalidaException
        extends RuntimeException {

    public RespuestaEvaluacionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
// Informa que una respuesta no es válida para la evaluación.