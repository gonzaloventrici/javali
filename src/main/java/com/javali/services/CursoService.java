package com.javali.services;

import com.javali.dtos.CursoRequest;
import com.javali.dtos.CursoResponse;

import java.util.List;

public interface CursoService {

    List<CursoResponse> listar();

    CursoResponse obtener(Long id);
    CursoResponse crear(CursoRequest request);
}
// Define las operaciones disponibles para trabajar con cursos.