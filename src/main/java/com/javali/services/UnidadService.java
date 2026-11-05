package com.javali.services;

import java.util.List;

import com.javali.dtos.UnidadRequest;
import com.javali.dtos.UnidadResponse;

public interface UnidadService {
    List<UnidadResponse>listar();
    UnidadResponse obtener(Long id);
    UnidadResponse crear(UnidadRequest request);
}

//Define el contrato del servicio de unidades.
//Declara las operaciones disponibles para trabajar con unidades.
//Permite listar todas las unidades (listar).
//Permite obtener una unidad por su identificador (obtener).
//Permite crear una nueva unidad a partir de un UnidadRequest (crear).
//Se utiliza para desacoplar la lógica de negocio de su implementación concreta (UnidadServiceImpl).
