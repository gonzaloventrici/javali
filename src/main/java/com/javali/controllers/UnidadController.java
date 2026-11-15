package com.javali.controllers;

import com.javali.dtos.UnidadRequest;
import com.javali.dtos.UnidadResponse;
import com.javali.services.UnidadService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UnidadController {

    private final UnidadService unidadService;

    public UnidadController(UnidadService unidadService) {
        this.unidadService = unidadService;
    }

    // Recibe el servicio mediante inyección por constructor.

    @GetMapping("/unidades")
    public ResponseEntity<List<UnidadResponse>> listar() {

        return ResponseEntity.ok(unidadService.listar());
    }

    // Devuelve todas las unidades.

    @GetMapping("/cursos/{cursoId}/unidades")
    public ResponseEntity<List<UnidadResponse>> listarPorCurso(
            @PathVariable Long cursoId) {

        return ResponseEntity.ok(
                unidadService.listarPorCurso(cursoId)
        );
    }

    // Devuelve las unidades pertenecientes a un curso.

    @GetMapping("/unidades/{id}")
    public ResponseEntity<UnidadResponse> obtener(
            @PathVariable Long id) {

        return ResponseEntity.ok(unidadService.obtener(id));
    }

    // Busca una unidad por su id.

    @PostMapping("/cursos/{cursoId}/unidades")
    public ResponseEntity<UnidadResponse> crear(
            @PathVariable Long cursoId,
            @Valid @RequestBody UnidadRequest request) {

        UnidadResponse unidadCreada =
                unidadService.crear(cursoId, request);

        URI ubicacion = URI.create(
                "/api/unidades/" + unidadCreada.id()
        );

        return ResponseEntity
                .created(ubicacion)
                .body(unidadCreada);
    }

    // Crea una unidad dentro del curso indicado.
}