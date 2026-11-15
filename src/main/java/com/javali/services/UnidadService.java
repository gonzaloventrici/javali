package com.javali.services;

import com.javali.dtos.UnidadRequest;
import com.javali.dtos.UnidadResponse;

import java.util.List;

public interface UnidadService {

    List<UnidadResponse> listar();
    List<UnidadResponse> listarPorCurso(Long cursoId);
    UnidadResponse obtener(Long id);
    UnidadResponse crear(
            Long cursoId,
            UnidadRequest request
    );
}


//Define el contrato del servicio de unidades.
//Declara las operaciones disponibles para trabajar con unidades.
//Permite listar todas las unidades (listar).
//Permite obtener una unidad por su identificador (obtener).
//Permite crear una nueva unidad a partir de un UnidadRequest (crear).
//Se utiliza para desacoplar la lógica de negocio de su implementación concreta (UnidadServiceImpl).
