package com.javali.exceptions;

public class LeccionNoEncontradaException extends RuntimeException {

    public LeccionNoEncontradaException(Long id) {
        super("No existe la lección " + id);
    }
}

// Informa que no se encontró una lección con el id solicitado.