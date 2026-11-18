package com.javali.dtos;

public record EstadoUnidadResponse(
        Long unidadId,
        EstadoAvance estado,
        int leccionesTotales,
        int leccionesCompletadas
) {
}
// Devuelve si una unidad está completada, en curso, bloqueada o por llegar.
