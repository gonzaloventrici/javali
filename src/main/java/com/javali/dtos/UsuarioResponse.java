package com.javali.dtos;

import com.javali.models.RolUsuario;

public record UsuarioResponse(
        Long id,
        String nombre,
        String email,
        RolUsuario rol
) {
}
// Devuelve los datos públicos del usuario sin mostrar la contraseña.