package com.javali.dtos;

import com.javali.models.CategoriaError;

public record RetroalimentacionResponse(
        Long id,
        Long ejercicioId,
        Integer indiceOpcion,
        CategoriaError categoriaError,
        String explicacion,
        String pista
) {
}
// Devuelve la retroalimentación guardada.