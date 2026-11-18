package com.javali.dtos;

public record EstadoCursoResponse(
        Long cursoId,
        boolean completado,
        int unidadesTotales,
        int unidadesCompletadas,
        int leccionesTotales,
        int leccionesCompletadas
) {
}
// Devuelve cuánto avanzó un estudiante en un curso.
