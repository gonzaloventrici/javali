package com.javali.controllers;

import com.javali.dtos.LoginRequest;
import com.javali.dtos.LoginResponse;
import com.javali.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    // Recibe el servicio mediante inyección por constructor.

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> iniciarSesion(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                authService.iniciarSesion(request)
        );
    }
    // Valida las credenciales y devuelve el token JWT con 200 OK.
}