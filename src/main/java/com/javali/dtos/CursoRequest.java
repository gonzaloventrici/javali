package com.javali.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CursoRequest(

        @NotBlank
        @Size(max = 100)
        String titulo,

        @NotBlank
        @Size(max = 1000)
        String descripcion,

        @NotNull
        Boolean activo
) {
}
// Recibe y valida los datos necesarios para crear un curso.