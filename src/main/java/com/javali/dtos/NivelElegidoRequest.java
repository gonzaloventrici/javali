package com.javali.dtos;

import com.javali.models.NivelCurso;
import jakarta.validation.constraints.NotNull;

public record NivelElegidoRequest(

        @NotNull
        NivelCurso nivel
) {
}
// Recibe el nivel que finalmente selecciona el estudiante.