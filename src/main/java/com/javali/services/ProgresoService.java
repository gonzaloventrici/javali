package com.javali.services;

import com.javali.dtos.ProgresoResponse;
import com.javali.models.Ejercicio;
import com.javali.models.Usuario;

import java.util.List;

public interface ProgresoService {

    void actualizar(
            Usuario usuario,
            Ejercicio ejercicio,
            boolean correcta
    );

    List<ProgresoResponse> listarPorUsuario(Long usuarioId);
}
// Define las operaciones para actualizar y consultar el progreso.