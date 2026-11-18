package com.javali.services;

import com.javali.dtos.PreferenciasRequest;
import com.javali.dtos.PreferenciasResponse;

public interface PreferenciasService {

    PreferenciasResponse obtener(Long usuarioId);
    // Devuelve los ajustes guardados o los valores por defecto.

    PreferenciasResponse guardar(Long usuarioId, PreferenciasRequest request);
    // Crea o actualiza los ajustes de accesibilidad del usuario.
}
