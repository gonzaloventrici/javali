package com.javali.exceptions;

public class PreguntaNoEncontradaException
        extends RuntimeException {

    public PreguntaNoEncontradaException(Long id) {
        super("No existe la pregunta " + id);
    }
}
// Informa que no existe una pregunta con el id solicitado.