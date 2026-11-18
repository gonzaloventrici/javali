package com.javali.repositories;

import com.javali.models.Evaluacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluacionRepository
        extends JpaRepository<Evaluacion, Long> {

    List<Evaluacion> findByUsuario_IdOrderByFechaDesc(Long usuarioId);

    boolean existsByUsuario_IdAndFinalizadaTrueAndNivelElegidoNotNull(
            Long usuarioId
    ); // Indica si el usuario terminó la evaluación y eligió su nivel.
}

// Permite guardar evaluaciones y consultar las de un usuario.