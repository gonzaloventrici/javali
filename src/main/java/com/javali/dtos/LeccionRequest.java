package com.javali.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LeccionRequest(
        @NotBlank
        @Size(max = 100)
        String titulo,
        @Size(max = 4000)
        String explicacion,
        @Min(1)
        int orden) {}
// Recibe y valida los datos necesarios para crear una lección.