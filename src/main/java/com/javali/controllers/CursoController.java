package com.javali.controllers;

import com.javali.dtos.CursoRequest;
import com.javali.dtos.CursoResponse;
import com.javali.services.CursoService;
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
@RequestMapping("/api/cursos")
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }
    // Recibe el servicio mediante inyección por constructor.

    @GetMapping
    public ResponseEntity<List<CursoResponse>> listar() {

        return ResponseEntity.ok(cursoService.listar());
    }
    // Devuelve todos los cursos con estado 200 OK.

    @GetMapping("/{id}")
    public ResponseEntity<CursoResponse> obtener(
            @PathVariable Long id) {

        return ResponseEntity.ok(cursoService.obtener(id));
    }
    // Devuelve el curso solicitado por id.

    @PostMapping
    public ResponseEntity<CursoResponse> crear(
            @Valid @RequestBody CursoRequest request) {

        CursoResponse cursoCreado = cursoService.crear(request);

        URI ubicacion = URI.create(
                "/api/cursos/" + cursoCreado.id()
        );

        return ResponseEntity
                .created(ubicacion)
                .body(cursoCreado);
    }
    // Crea un curso y devuelve 201 Created.
}