package com.javali.exceptions;

public class EvaluacionNoEncontradaException
        extends RuntimeException {

    public EvaluacionNoEncontradaException(Long id) {
        super("No existe la evaluación " + id);
    }
}
// Informa que no existe una evaluación con el id solicitado.