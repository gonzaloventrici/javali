package com.javali.services;

import com.javali.dtos.CursoRequest;
import com.javali.dtos.CursoResponse;

import java.util.List;

public interface CursoService {

    List<CursoResponse> listar();

    CursoResponse obtener(Long id);
    CursoResponse crear(CursoRequest request);
    CursoResponse modificar(Long id,CursoRequest request);
    void eliminar(Long id);
}
// Define el ABML completo de cursos.