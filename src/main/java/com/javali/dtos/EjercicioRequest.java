package com.javali.dtos;

import com.javali.models.TipoEjercicio;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record EjercicioRequest(
        @NotNull
        TipoEjercicio tipo,
        @NotBlank
        @Size(max = 1000)
        String enunciado,
        @Size(max = 4000)
        String codigo,
        @Size(max = 1000)
        String explicacion,
        @Size(max = 500)
        String pista,
        @Min(1)
        int orden,
        List<String> opciones,
        Integer indiceCorrecto) {}
// Recibe y valida los datos necesarios para crear un ejercicio.