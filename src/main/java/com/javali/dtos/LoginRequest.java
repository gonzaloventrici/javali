package com.javali.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank
        @Email
        String email,

        @NotBlank
        String password
) {
}
// Recibe las credenciales para iniciar sesión y obtener un token JWT.