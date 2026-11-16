package com.javali.services;

import com.javali.dtos.EvaluacionResponse;
import com.javali.dtos.FinalizarEvaluacionRequest;
import com.javali.dtos.NivelElegidoRequest;
import com.javali.dtos.PreguntaRequest;
import com.javali.dtos.PreguntaResponse;

import java.util.List;

public interface EvaluacionService {

    EvaluacionResponse iniciar(Long usuarioId); // Crea una evaluación nueva para un usuario.

    PreguntaResponse agregarPregunta(
            Long evaluacionId,
            PreguntaRequest request
    ); // Agrega una pregunta a una evaluación.
    List<PreguntaResponse> listarPreguntas(Long evaluacionId); 
    // Devuelve las preguntas sin mostrar las respuestas correctas.

    EvaluacionResponse finalizar(
            Long evaluacionId,
            FinalizarEvaluacionRequest request
    ); // Corrige las respuestas y calcula el nivel recomendado.

    EvaluacionResponse elegirNivel(
            Long evaluacionId,
            NivelElegidoRequest request
    ); // Permite aceptar o cambiar el nivel recomendado.
    List<EvaluacionResponse> listarPorUsuario(Long usuarioId);
}
// Define las operaciones necesarias para realizar una evaluación.