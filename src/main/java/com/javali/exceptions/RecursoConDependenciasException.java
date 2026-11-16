package com.javali.exceptions;

public class RecursoConDependenciasException
        extends RuntimeException {

    public RecursoConDependenciasException(String mensaje) {
        super(mensaje);
    }
}

//Evita que SQL Server termine produciendo un error genérico por una 
//clave foránea.