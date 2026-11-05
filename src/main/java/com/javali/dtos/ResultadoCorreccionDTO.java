package com.javali.dtos;

public record ResultadoCorreccionDTO(
        boolean correcta,
        String explicacion,
        String pista) {}
// Devuelve el resultado, la explicación y una pista.