package com.javali.controllers;

import com.javali.dtos.RetroalimentacionRequest;
import com.javali.dtos.RetroalimentacionResponse;
import com.javali.services.RetroalimentacionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api")
public class RetroalimentacionController {

    private final RetroalimentacionService retroalimentacionService;

    public RetroalimentacionController(
            RetroalimentacionService retroalimentacionService) {

        this.retroalimentacionService = retroalimentacionService;
    }
    // Recibe el servicio mediante inyección por constructor.

    @PostMapping("/ejercicios/{ejercicioId}/retroalimentaciones")
    public ResponseEntity<RetroalimentacionResponse> crear(
            @PathVariable Long ejercicioId,
            @Valid @RequestBody RetroalimentacionRequest request) {

        RetroalimentacionResponse creada =
                retroalimentacionService.crear(ejercicioId, request);

        URI ubicacion = URI.create(
                "/api/ejercicios/" + ejercicioId
                        + "/retroalimentaciones/" + creada.id()
        );

        return ResponseEntity
                .created(ubicacion)
                .body(creada);
    }
    // Crea una retroalimentación y devuelve 201 Created.
}