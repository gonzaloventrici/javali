package com.javali.dtos;

import com.javali.models.NivelContraste;
import com.javali.models.TamanoLetra;
import com.javali.models.Tipografia;

public record PreferenciasResponse(
        TamanoLetra tamanoLetra,
        NivelContraste contraste,
        Tipografia tipografia,
        boolean mostrarRacha) {

    // Valores usados cuando el estudiante todavía no guardó ajustes.
    public static PreferenciasResponse porDefecto() {
        return new PreferenciasResponse(
                TamanoLetra.NORMAL,
                NivelContraste.NORMAL,
                Tipografia.SISTEMA,
                true
        );
    }
}
// Devuelve los ajustes de accesibilidad del estudiante.
