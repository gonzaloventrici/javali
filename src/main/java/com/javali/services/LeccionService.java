package com.javali.services;

import com.javali.dtos.LeccionRequest;
import com.javali.dtos.LeccionResponse;

import java.util.List;

public interface LeccionService {
    List<LeccionResponse> listarPorUnidad(Long unidadId);
    LeccionResponse obtener(Long id);
    LeccionResponse crear(Long unidadId, LeccionRequest request);
}
// Define las operaciones para listar, obtener y crear lecciones.
