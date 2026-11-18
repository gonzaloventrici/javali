package com.javali.exceptions;

public class CursoNoEncontradoException extends RuntimeException {

    public CursoNoEncontradoException(Long id) {
        super("No existe el curso " + id);
    }
}
// Informa que no existe un curso con el identificador solicitado.