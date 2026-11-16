package com.javali.controllers;

import com.javali.dtos.UsuarioRequest;
import com.javali.dtos.UsuarioResponse;
import com.javali.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
    // Recibe el servicio mediante inyección por constructor.

    @PostMapping
    public ResponseEntity<UsuarioResponse> registrar(
            @Valid @RequestBody UsuarioRequest request) {

        UsuarioResponse usuarioRegistrado =
                usuarioService.registrar(request);

        URI ubicacion = URI.create(
                "/api/usuarios/" + usuarioRegistrado.id()
        );

        return ResponseEntity
                .created(ubicacion)
                .body(usuarioRegistrado);
    }
    // Registra un usuario y devuelve 201 Created.

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtener(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                usuarioService.obtener(id)
        );
    }
    // Busca un usuario por id y devuelve 200 OK.
}