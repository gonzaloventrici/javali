package com.javali.services;

import com.javali.dtos.ProgresoResponse;
import com.javali.dtos.ResumenProgresoResponse;
import com.javali.models.Ejercicio;
import com.javali.models.Usuario;
import com.javali.dtos.EstadoLeccionResponse;

import java.util.List;

public interface ProgresoService {

    void actualizar(
            Usuario usuario,
            Ejercicio ejercicio,
            boolean correcta
    );

    List<ProgresoResponse> listarPorUsuario(Long usuarioId);

    ResumenProgresoResponse obtenerResumen(Long usuarioId);
    // Calcula fortalezas, dificultades y recomendaciones del usuario.

    EstadoLeccionResponse obtenerEstadoLeccion(
        Long usuarioId,
        Long leccionId
    ); // Determina si una lección está desbloqueada para el usuario.
}
// Define la actualización, consulta y resumen del progreso.