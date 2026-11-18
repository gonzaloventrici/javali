package com.javali.dtos;

import jakarta.validation.constraints.Max;
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
        Integer indiceCorrecto,
        @Min(1)
        @Max(3)
        Integer dificultad
) {

    // Constructor para preguntas sin dificultad indicada (cuentan como básicas).
    public PreguntaRequest(
            String enunciado,
            String codigo,
            int orden,
            List<String> opciones,
            Integer indiceCorrecto) {
        this(enunciado, codigo, orden, opciones, indiceCorrecto, 1);
    }
}
// Recibe y valida los datos necesarios para crear una pregunta.
// La dificultad (1 a 3) define cuánto pesa la pregunta en el puntaje.
