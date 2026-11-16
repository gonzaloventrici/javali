package com.javali.exceptions;

public class UsuarioNoEncontradoException extends RuntimeException {

    public UsuarioNoEncontradoException(Long id) {
        super("No existe el usuario " + id);
    }
}
// Informa que no existe un usuario con el id solicitado.