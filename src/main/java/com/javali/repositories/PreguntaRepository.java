package com.javali.repositories;

import com.javali.models.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreguntaRepository
        extends JpaRepository<Pregunta, Long> {

    List<Pregunta> findByEvaluacion_IdOrderByOrdenAsc(
            Long evaluacionId
    );
}
// Devuelve las preguntas de una evaluación ordenadas.