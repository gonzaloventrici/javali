package com.javali.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record FinalizarEvaluacionRequest(

        @NotEmpty
        List<@Valid RespuestaPreguntaDTO> respuestas
) {
}
// Recibe todas las respuestas necesarias para finalizar la evaluación.