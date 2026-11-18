package com.javali.dtos;

import com.javali.validators.PasswordSegura;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(

        @NotBlank
        @Size(max = 100)
        String nombre,

        @NotBlank
        @Email
        @Size(max = 150)
        String email,

        @NotBlank
        @Size(min = 8, max = 100)
        @PasswordSegura
        String password
) {
}
// Recibe y valida los datos necesarios para registrar un usuario.