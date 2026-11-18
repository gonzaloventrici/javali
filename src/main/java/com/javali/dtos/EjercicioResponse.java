package com.javali.dtos;

import com.javali.models.TipoEjercicio;

import java.util.List;

public record EjercicioResponse(
        Long id,
        Long leccionId,
        TipoEjercicio tipo,
        String enunciado,
        String codigo,
        String explicacion,
        String pista,
        int orden,
        List<String> opciones) {}
// Devuelve los datos del ejercicio sin mostrar la respuesta correcta.