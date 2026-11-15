package com.javali.exceptions;

public class EmailYaRegistradoException extends RuntimeException {

    public EmailYaRegistradoException(String email) {
        super("Ya existe un usuario con el email " + email);
    }
}
// Informa que el correo electrónico ya está registrado.