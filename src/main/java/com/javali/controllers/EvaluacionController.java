package com.javali.controllers;

import com.javali.dtos.EvaluacionResponse;
import com.javali.dtos.FinalizarEvaluacionRequest;
import com.javali.dtos.NivelElegidoRequest;
import com.javali.dtos.PreguntaRequest;
import com.javali.dtos.PreguntaResponse;
import com.javali.services.EvaluacionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class EvaluacionController {

    private final EvaluacionService evaluacionService;

    public EvaluacionController(
            EvaluacionService evaluacionService) {

        this.evaluacionService = evaluacionService;
    }
    // Recibe el servicio mediante inyección por constructor.

    @PostMapping("/usuarios/{usuarioId}/evaluaciones")
    public ResponseEntity<EvaluacionResponse> iniciar(
            @PathVariable Long usuarioId) {

        EvaluacionResponse evaluacion =
                evaluacionService.iniciar(usuarioId);

        URI ubicacion = URI.create(
                "/api/evaluaciones/" + evaluacion.id()
        );

        return ResponseEntity
                .created(ubicacion)
                .body(evaluacion);
    }
    // Inicia una evaluación para el usuario y devuelve 201 Created.

    @PostMapping("/evaluaciones/{evaluacionId}/preguntas")
    public ResponseEntity<PreguntaResponse> agregarPregunta(
            @PathVariable Long evaluacionId,
            @Valid @RequestBody PreguntaRequest request) {

        PreguntaResponse pregunta =
                evaluacionService.agregarPregunta(
                        evaluacionId,
                        request
                );

        URI ubicacion = URI.create(
                "/api/evaluaciones/" + evaluacionId
                        + "/preguntas/" + pregunta.id()
        );

        return ResponseEntity
                .created(ubicacion)
                .body(pregunta);
    }
    // Agrega una pregunta a la evaluación.

    @GetMapping("/evaluaciones/{evaluacionId}/preguntas")
    public ResponseEntity<List<PreguntaResponse>> listarPreguntas(
            @PathVariable Long evaluacionId) {

        return ResponseEntity.ok(
                evaluacionService.listarPreguntas(evaluacionId)
        );
    }
    // Devuelve las preguntas sin revelar las respuestas correctas.

    @PostMapping("/evaluaciones/{evaluacionId}/finalizacion")
    public ResponseEntity<EvaluacionResponse> finalizar(
            @PathVariable Long evaluacionId,
            @Valid @RequestBody FinalizarEvaluacionRequest request) {

        return ResponseEntity.ok(
                evaluacionService.finalizar(
                        evaluacionId,
                        request
                )
        );
    }
    // Corrige las respuestas y recomienda un nivel.

    @PatchMapping("/evaluaciones/{evaluacionId}/nivel")
    public ResponseEntity<EvaluacionResponse> elegirNivel(
            @PathVariable Long evaluacionId,
            @Valid @RequestBody NivelElegidoRequest request) {

        return ResponseEntity.ok(
                evaluacionService.elegirNivel(
                        evaluacionId,
                        request
                )
        );
    }
    // Guarda el nivel elegido por el estudiante.

    @GetMapping("/usuarios/{usuarioId}/evaluaciones")
    public ResponseEntity<List<EvaluacionResponse>> listarPorUsuario(
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(
                evaluacionService.listarPorUsuario(usuarioId)
        );
    }
    // Devuelve el historial de evaluaciones del usuario.
}