package com.javali.dtos;

public record LeccionResponse(
        Long id,
        Long unidadId,
        String titulo,
        String explicacion,
        int orden) {}
// Devuelve los datos de una lección sin exponer directamente la entidad.