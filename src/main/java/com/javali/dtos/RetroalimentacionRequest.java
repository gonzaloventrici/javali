package com.javali.dtos;

import com.javali.models.CategoriaError;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RetroalimentacionRequest(

        @NotNull
        @Min(0)
        Integer indiceOpcion,

        @NotNull
        CategoriaError categoriaError,

        @NotBlank
        @Size(max = 1000)
        String explicacion,

        @NotBlank
        @Size(max = 500)
        String pista
) {
}

// Recibe y valida la retroalimentación de una opción incorrecta.