package com.javali.services.impl;

import com.javali.dtos.LoginRequest;
import com.javali.dtos.LoginResponse;
import com.javali.exceptions.CredencialesInvalidasException;
import com.javali.models.RolUsuario;
import com.javali.models.Usuario;
import com.javali.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtEncoder jwtEncoder;

    private AuthServiceImpl authService;

    @BeforeEach
    void preparar() {
        authService = new AuthServiceImpl(
                usuarioRepository,
                passwordEncoder,
                jwtEncoder,
                60
        );
    }

    private Usuario crearUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(2L);
        usuario.setNombre("Estudiante Demo");
        usuario.setEmail("estudiante@javali.com");
        usuario.setPasswordHash("hash");
        usuario.setRol(RolUsuario.ESTUDIANTE);
        return usuario;
    }

    @Test
    void iniciarSesionDevuelveTokenConCredencialesCorrectas() {
        when(usuarioRepository.findByEmail("estudiante@javali.com"))
                .thenReturn(Optional.of(crearUsuario()));
        when(passwordEncoder.matches("Estudiante123", "hash"))
                .thenReturn(true);
        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(
                        Jwt.withTokenValue("token-firmado")
                                .header("alg", "HS256")
                                .claim("sub", "estudiante@javali.com")
                                .build()
                );

        LoginResponse respuesta = authService.iniciarSesion(
                new LoginRequest(
                        " Estudiante@Javali.com ",
                        "Estudiante123"
                )
        );

        assertEquals("token-firmado", respuesta.token());
        assertEquals("Bearer", respuesta.tipo());
        assertEquals(3600, respuesta.expiraEnSegundos());
        assertEquals(2L, respuesta.usuarioId());
        assertEquals("estudiante@javali.com", respuesta.email());
        assertEquals(RolUsuario.ESTUDIANTE, respuesta.rol());
    }

    @Test
    void iniciarSesionFallaConPasswordIncorrecta() {
        when(usuarioRepository.findByEmail("estudiante@javali.com"))
                .thenReturn(Optional.of(crearUsuario()));
        when(passwordEncoder.matches("incorrecta", "hash"))
                .thenReturn(false);

        assertThrows(
                CredencialesInvalidasException.class,
                () -> authService.iniciarSesion(
                        new LoginRequest(
                                "estudiante@javali.com",
                                "incorrecta"
                        )
                )
        );
        verify(jwtEncoder, never())
                .encode(any(JwtEncoderParameters.class));
    }

    @Test
    void iniciarSesionFallaConEmailInexistente() {
        when(usuarioRepository.findByEmail("nadie@javali.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                CredencialesInvalidasException.class,
                () -> authService.iniciarSesion(
                        new LoginRequest(
                                "nadie@javali.com",
                                "cualquiera"
                        )
                )
        );
    }
}
