package com.javali.controllers;

import com.javali.dtos.IntentoResponse;
import com.javali.services.IntentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api")
public class IntentoController {

    private final IntentoService intentoService;

    public IntentoController(IntentoService intentoService) {
        this.intentoService = intentoService;
    }

    // Recibe el servicio mediante inyección por constructor.

    @PreAuthorize(
        "@autorizacionUsuarioService.puedeAcceder(authentication, #usuarioId)")
    @GetMapping("/usuarios/{usuarioId}/intentos")
    public ResponseEntity<List<IntentoResponse>> listarPorUsuario(
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(
                intentoService.listarPorUsuario(usuarioId)
        );
    }
    // Devuelve el historial de intentos de un usuario.
}