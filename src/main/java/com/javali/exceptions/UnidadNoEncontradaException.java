package com.javali.exceptions;

public class UnidadNoEncontradaException extends RuntimeException {
    public UnidadNoEncontradaException(Long id){
        super("No existe la unidad "+id); 
    }
}

//Define una excepción personalizada para el caso en que se solicita una unidad inexistente.
//Hereda de RuntimeException, por lo que puede propagarse sin necesidad de declarar throws.
//Recibe el identificador de la unidad que no fue encontrada.
//Genera un mensaje descriptivo indicando qué unidad no existe.
//Se utiliza en el servicio para informar errores de búsqueda de manera clara y centralizada.