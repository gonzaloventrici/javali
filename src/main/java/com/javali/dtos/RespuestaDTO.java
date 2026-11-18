package com.javali.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RespuestaDTO(
        @NotNull
        Long usuarioId,

        @Min(0)
        Integer indiceSeleccionado,

        @Size(max = 50)
        List<Integer> ordenSeleccionado) {

    // Constructor para respuestas de una sola opción.
    public RespuestaDTO(Long usuarioId, Integer indiceSeleccionado) {
        this(usuarioId, indiceSeleccionado, null);
    }
}
// Recibe el usuario y la respuesta: una opción elegida
// o, en los ejercicios de ordenar, la secuencia de índices.
// El servicio comprueba que llegue el dato que corresponde al tipo.
