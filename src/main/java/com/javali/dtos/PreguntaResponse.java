package com.javali.dtos;

import java.util.List;

public record PreguntaResponse(
        Long id,
        Long evaluacionId,
        String enunciado,
        String codigo,
        int orden,
        List<String> opciones
) {
}
// Devuelve una pregunta sin mostrar cuál es la opción correcta.