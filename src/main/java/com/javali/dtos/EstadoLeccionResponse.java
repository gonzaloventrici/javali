package com.javali.dtos;

public record EstadoLeccionResponse(
        Long leccionId,
        String titulo,
        boolean desbloqueada,
        boolean completada,
        double porcentajeAciertos
) {
} // Devuelve el estado de acceso y avance de una lección.