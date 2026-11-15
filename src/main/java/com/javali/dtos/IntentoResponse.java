package com.javali.dtos;

import com.javali.models.CategoriaError;

import java.time.LocalDateTime;

public record IntentoResponse(
        Long id,
        Long usuarioId,
        Long ejercicioId,
        Integer indiceSeleccionado,
        boolean correcta,
        CategoriaError categoriaError,
        LocalDateTime fecha
) {
}
// Devuelve los datos registrados de un intento.