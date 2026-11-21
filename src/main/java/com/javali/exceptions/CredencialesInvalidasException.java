package com.javali.exceptions;

public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException() {
        super("Usuario o contraseña incorrectos");
    }
}
// Informa que el email o la contraseña no son correctos.