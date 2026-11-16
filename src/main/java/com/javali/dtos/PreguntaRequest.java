package com.javali.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PreguntaRequest(

        @NotBlank
        @Size(max = 1000)
        String enunciado,

        @Size(max = 4000)
        String codigo,

        @Min(1)
        int orden,

        @NotNull
        @Size(min = 2)
        List<@NotBlank @Size(max = 500) String> opciones,

        @NotNull
        @Min(0)
        Integer indiceCorrecto
) {
}
// Recibe y valida los datos necesarios para crear una pregunta.