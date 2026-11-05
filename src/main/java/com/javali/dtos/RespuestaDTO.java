package com.javali.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RespuestaDTO(
        @NotNull
        @Min(0)
        Integer indiceSeleccionado) {}
// Recibe el índice de la opción elegida por el estudiante.