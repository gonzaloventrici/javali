package com.javali.dtos;

import java.time.LocalDateTime;

public record ProgresoResponse(
        Long id,
        Long usuarioId,
        Long leccionId,
        String tituloLeccion,
        int cantidadIntentos,
        int respuestasCorrectas,
        double porcentajeAciertos,
        boolean completada,
        LocalDateTime ultimaActividad
) {
}
// Devuelve el avance del usuario en una lección.