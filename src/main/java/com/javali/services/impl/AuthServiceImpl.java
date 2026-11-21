package com.javali.services.impl;

import com.javali.dtos.LoginRequest;
import com.javali.dtos.LoginResponse;
import com.javali.exceptions.CredencialesInvalidasException;
import com.javali.models.Usuario;
import com.javali.repositories.UsuarioRepository;
import com.javali.services.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final Duration duracionToken;

    public AuthServiceImpl(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtEncoder jwtEncoder,
            @Value("${jwt.expiracion-minutos}") long expiracionMinutos) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.duracionToken = Duration.ofMinutes(expiracionMinutos);
    }
    // Recibe el repositorio, el codificador, el firmador de tokens y la duración.

    @Override
    public LoginResponse iniciarSesion(LoginRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase();

        Usuario usuario = usuarioRepository
                .findByEmail(email)
                .orElseThrow(CredencialesInvalidasException::new);

        if (!passwordEncoder.matches(
                request.password(),
                usuario.getPasswordHash())) {
            throw new CredencialesInvalidasException();
        }

        Instant ahora = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("javali")
                .subject(usuario.getEmail())
                .issuedAt(ahora)
                .expiresAt(ahora.plus(duracionToken))
                .claim("rol", usuario.getRol().name())
                .build();

        JwsHeader encabezado = JwsHeader
                .with(MacAlgorithm.HS256)
                .build();

        String token = jwtEncoder
                .encode(JwtEncoderParameters.from(encabezado, claims))
                .getTokenValue();

        return new LoginResponse(
                token,
                "Bearer",
                duracionToken.toSeconds(),
                usuario.getId(),
                usuario.getEmail(),
                usuario.getRol()
        );
    }
    // Verifica las credenciales y firma un token con el email y el rol.
}