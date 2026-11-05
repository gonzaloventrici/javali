package com.javali.controllers;

import com.javali.dtos.EjercicioRequest;
import com.javali.dtos.EjercicioResponse;
import com.javali.services.EjercicioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.javali.dtos.RespuestaDTO;
import com.javali.dtos.ResultadoCorreccionDTO;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class EjercicioController {

    private final EjercicioService ejercicioService;

    public EjercicioController(EjercicioService ejercicioService) {
        this.ejercicioService = ejercicioService;
    }
    // Recibe el servicio mediante inyección por constructor.

    @GetMapping("/lecciones/{leccionId}/ejercicios")
    public ResponseEntity<List<EjercicioResponse>> listarPorLeccion(
            @PathVariable Long leccionId) {
        return ResponseEntity.ok(
                ejercicioService.listarPorLeccion(leccionId)
        );
    }
    // Devuelve los ejercicios de una lección.

    @GetMapping("/ejercicios/{id}")
    public ResponseEntity<EjercicioResponse> obtener(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ejercicioService.obtener(id)
        );
    }
    // Devuelve un ejercicio buscado por id.

    @PostMapping("/lecciones/{leccionId}/ejercicios")
    public ResponseEntity<EjercicioResponse> crear(
            @PathVariable Long leccionId,
            @Valid @RequestBody EjercicioRequest request) {
        EjercicioResponse ejercicioCreado =
                ejercicioService.crear(leccionId, request);
        URI ubicacion = URI.create(
                "/api/ejercicios/" + ejercicioCreado.id()
        );
        return ResponseEntity
                .created(ubicacion)
                .body(ejercicioCreado);
    }
    // Crea un ejercicio y devuelve 201 Created.

    @PostMapping("/ejercicios/{id}/respuestas")
    public ResponseEntity<ResultadoCorreccionDTO> corregir(
            @PathVariable Long id,
            @Valid @RequestBody RespuestaDTO respuesta) {
        return ResponseEntity.ok(
                ejercicioService.corregir(id, respuesta)
        );
    }
    // Recibe una respuesta, la corrige y devuelve el resultado.
}