package com.javali.repositories;

import com.javali.models.Progreso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProgresoRepository
        extends JpaRepository<Progreso, Long> {

    Optional<Progreso> findByUsuario_IdAndLeccion_Id(
            Long usuarioId,
            Long leccionId
    );
    // Busca el progreso del usuario para una lección específica.

    List<Progreso> findByUsuario_IdOrderByUltimaActividadDesc(
            Long usuarioId
    );
    // Devuelve todos los progresos del usuario por actividad reciente.
}
// Permite consultar y guardar el progreso de un usuario.