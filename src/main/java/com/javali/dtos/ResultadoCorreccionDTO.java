package com.javali.dtos;

import com.javali.models.CategoriaError;

public record ResultadoCorreccionDTO(
        boolean correcta,
        CategoriaError categoriaError,
        String explicacion,
        String pista) {}
// Devuelve el resultado, el error, la explicación y una pista.