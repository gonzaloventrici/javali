package com.javali.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RespuestaDTO(
        @NotNull
        Long usuarioId,

        @NotNull
        @Min(0)
        Integer indiceSeleccionado) {}
// Recibe el usuario y la opción seleccionada.