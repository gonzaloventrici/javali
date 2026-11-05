package com.javali.services;

import com.javali.dtos.EjercicioRequest;
import com.javali.dtos.EjercicioResponse;
import com.javali.dtos.RespuestaDTO;
import com.javali.dtos.ResultadoCorreccionDTO;

import java.util.List;

public interface EjercicioService {
    List<EjercicioResponse> listarPorLeccion(Long leccionId);
    EjercicioResponse obtener(Long id);
    EjercicioResponse crear(
            Long leccionId,
            EjercicioRequest request
    );
    ResultadoCorreccionDTO corregir(
            Long id,
            RespuestaDTO respuesta
    );
}
// Define las operaciones disponibles para trabajar con ejercicios.