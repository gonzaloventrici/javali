package com.javali.dtos;

import com.javali.models.CategoriaError;

import java.util.List;

public record ResumenProgresoResponse(
        Long usuarioId,
        int totalIntentos,
        int respuestasCorrectas,
        double porcentajeGeneral,
        CategoriaError errorFrecuente,
        List<String> fortalezas,
        List<String> temasPendientes,
        String recomendacion
) {
}
// Devuelve un resumen del desempeño y las recomendaciones del usuario.