package com.javali.services;

import com.javali.dtos.EstadoCursoResponse;
import com.javali.dtos.EstadoLeccionResponse;
import com.javali.dtos.EstadoUnidadResponse;

import java.util.Map;

public interface AvanceService {

    Map<Long, EstadoCursoResponse> estadoCursos(Long usuarioId);
    // Avance del usuario en cada curso, por id de curso.

    EstadoCursoResponse estadoCurso(Long usuarioId, Long cursoId);
    // Avance del usuario en un curso.

    Map<Long, EstadoUnidadResponse> estadoUnidades(Long usuarioId, Long cursoId);
    // Estado de cada unidad de un curso, por id de unidad.

    Map<Long, EstadoLeccionResponse> estadoLecciones(Long usuarioId, Long unidadId);
    // Estado de cada lección de una unidad, por id de lección.

    boolean leccionDesbloqueada(Long usuarioId, Long leccionId);
    // Indica si el usuario puede abrir una lección.
}
// Calcula la ruta de aprendizaje: qué está completado, disponible o bloqueado.
