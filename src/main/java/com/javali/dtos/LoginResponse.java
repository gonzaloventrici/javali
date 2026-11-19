package com.javali.dtos;

import com.javali.models.RolUsuario;

public record LoginResponse(
        String token,
        String tipo,
        long expiraEnSegundos,
        Long usuarioId,
        String email,
        RolUsuario rol
) {
}
// Devuelve el token JWT y sus datos básicos tras un inicio de sesión correcto.