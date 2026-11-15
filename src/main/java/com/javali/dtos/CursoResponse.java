package com.javali.dtos;

public record CursoResponse(
        Long id,
        String titulo,
        String descripcion,
        boolean activo
) {
}
// Devuelve los datos públicos de un curso.