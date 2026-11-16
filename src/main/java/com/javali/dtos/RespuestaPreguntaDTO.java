package com.javali.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RespuestaPreguntaDTO(

        @NotNull
        Long preguntaId,

        @NotNull
        @Min(0)
        Integer indiceSeleccionado
) {
}
// Representa la respuesta del estudiante a una pregunta.
