package com.javali.dtos;

import com.javali.models.NivelCurso;

import java.time.LocalDateTime;

public record EvaluacionResponse(
        Long id,
        Long usuarioId,
        int respuestasCorrectas,
        int totalPreguntas,
        double porcentaje,
        NivelCurso nivelRecomendado,
        NivelCurso nivelElegido,
        boolean finalizada,
        LocalDateTime fecha
) {
}
// Devuelve el resultado y los niveles de una evaluación.
