package com.javali.dtos;

import com.javali.models.NivelContraste;
import com.javali.models.TamanoLetra;
import com.javali.models.Tipografia;
import jakarta.validation.constraints.NotNull;

public record PreferenciasRequest(
        @NotNull TamanoLetra tamanoLetra,
        @NotNull NivelContraste contraste,
        @NotNull Tipografia tipografia,
        boolean mostrarRacha) {}
// Recibe los ajustes de accesibilidad elegidos por el estudiante.
