package com.javali.exceptions;

public class EjercicioNoEncontradoException extends RuntimeException {

    public EjercicioNoEncontradoException(Long id) {
        super("No existe el ejercicio " + id);
    }
}
// Informa que no se encontró un ejercicio con el id solicitado.