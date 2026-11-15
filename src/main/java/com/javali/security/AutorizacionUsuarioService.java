package com.javali.security;

import com.javali.models.RolUsuario;
import com.javali.models.Usuario;
import com.javali.repositories.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service("autorizacionUsuarioService")
public class AutorizacionUsuarioService {

    private final UsuarioRepository usuarioRepository;

    public AutorizacionUsuarioService(
            UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

 //Comprueba si el usuario autenticado puede acceder a los datos del usuario indicado.
    // @param authentication autenticación actual
    // @param usuarioId identificador del usuario solicitado
    // @return true para el propietario o un administrador
    public boolean puedeAcceder(
            Authentication authentication,
            Long usuarioId) {
        if (authentication == null
                || !authentication.isAuthenticated()) {

            return false;
        }

        Usuario usuarioAutenticado = usuarioRepository
                .findByEmail(authentication.getName())
                .orElse(null);
        if (usuarioAutenticado == null) {
            return false;
        }
        if (usuarioAutenticado.getRol()
                == RolUsuario.ADMINISTRADOR) {

            return true;
        }
        return usuarioAutenticado.getId().equals(usuarioId);
    }
}