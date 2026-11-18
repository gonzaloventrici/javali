package com.javali.security;

import com.javali.models.Usuario;
import com.javali.repositories.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsServiceImpl
        implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsServiceImpl(
            UsuarioRepository usuarioRepository) {

        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        String emailNormalizado = email
                .trim()
                .toLowerCase();
        Usuario usuario = usuarioRepository
                .findByEmail(emailNormalizado)
                .orElseThrow(
                        () -> new UsernameNotFoundException(
                                "Usuario o contraseña incorrectos"
                        )
                );
        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPasswordHash())
                .roles(usuario.getRol().name())
                .build();
    }
}

//Email recibido
//busca el usuario
//obtiene el hash BCrypt
//convierte el rol en una autoridad
//Spring Security verifica la contraseña