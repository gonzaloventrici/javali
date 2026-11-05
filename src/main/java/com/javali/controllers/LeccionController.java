package com.javali.controllers;

import com.javali.dtos.LeccionRequest;
import com.javali.dtos.LeccionResponse;
import com.javali.services.LeccionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class LeccionController {

    private final LeccionService leccionService;

    public LeccionController(LeccionService leccionService) {
        this.leccionService = leccionService;
    }
    // Recibe el servicio de lecciones mediante inyección por constructor.

    @GetMapping("/unidades/{unidadId}/lecciones")
    public ResponseEntity<List<LeccionResponse>> listarPorUnidad(
            @PathVariable Long unidadId) {

        return ResponseEntity.ok(
                leccionService.listarPorUnidad(unidadId)
        );
    }
    // Devuelve las lecciones de una unidad con estado 200 OK.

    @GetMapping("/lecciones/{id}")
    public ResponseEntity<LeccionResponse> obtener(
            @PathVariable Long id) {

        return ResponseEntity.ok(leccionService.obtener(id));
    }
    // Busca una lección por su id y la devuelve con estado 200 OK.

    @PostMapping("/unidades/{unidadId}/lecciones")
    public ResponseEntity<LeccionResponse> crear(
            @PathVariable Long unidadId,
            @Valid @RequestBody LeccionRequest request) {

        LeccionResponse leccionCreada =
                leccionService.crear(unidadId, request);

        URI ubicacion = URI.create(
                "/api/lecciones/" + leccionCreada.id()
        );

        return ResponseEntity
                .created(ubicacion)
                .body(leccionCreada);
    }
    // Valida los datos, crea la lección y devuelve 201 Created.
}