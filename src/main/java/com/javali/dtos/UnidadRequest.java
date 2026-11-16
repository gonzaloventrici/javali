package com.javali.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UnidadRequest(
    @NotBlank @Size(max= 100) String titulo,
    @Size(max = 500) String description,
    @Min (1) int orden){ }

//DTO utilizado para recibir los datos necesarios para crear una unidad.
//Valida que el título no esté vacío y tenga como máximo 100 caracteres.
//Valida que la descripción no supere los 500 caracteres.
//Valida que el orden sea mayor o igual a 1.