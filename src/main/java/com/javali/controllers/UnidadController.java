// Expone los endpoints REST para listar, buscar y crear unidades.
package com.javali.controllers;

import com.javali.dtos.UnidadRequest;
import com.javali.dtos.UnidadResponse;
import com.javali.services.UnidadService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/unidades")
public class UnidadController {

    private final UnidadService unidadService;
    
    public UnidadController(UnidadService unidadService) {
        this.unidadService = unidadService;
    }
    // Recibe el servicio de unidades mediante inyección por constructor.

    @GetMapping
    public ResponseEntity<List<UnidadResponse>> listar() {
        return ResponseEntity.ok(unidadService.listar());
    }
    // Devuelve todas las unidades ordenadas con estado 200 OK.

    @GetMapping("/{id}")
    public ResponseEntity<UnidadResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(unidadService.obtener(id));
    }
    // Busca una unidad por su id y la devuelve con estado 200 OK.

    @PostMapping
    public ResponseEntity<UnidadResponse> crear(
            @Valid @RequestBody UnidadRequest request) {

        UnidadResponse unidadCreada = unidadService.crear(request);

        URI ubicacion = URI.create(
                "/api/unidades/" + unidadCreada.id()
        );

        return ResponseEntity
                .created(ubicacion)
                .body(unidadCreada);
    }
    // Valida los datos, crea la unidad y devuelve 201 Created con su ubicación.
}