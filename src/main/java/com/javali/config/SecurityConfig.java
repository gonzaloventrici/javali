package com.javali.config;

import com.javali.security.UsuarioDetailsServiceImpl;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private final UsuarioDetailsServiceImpl usuarioDetailsService;

    public SecurityConfig(
            UsuarioDetailsServiceImpl usuarioDetailsService) {
        this.usuarioDetailsService = usuarioDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Clave HMAC compartida para firmar y validar los tokens JWT.
    @Bean
    public SecretKey claveJwt(
            @Value("${jwt.secret}") String secreto) {
        byte[] bytes = secreto.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException(
                    "jwt.secret debe tener al menos 32 caracteres"
            );
        }
        return new SecretKeySpec(bytes, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey claveJwt) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(claveJwt));
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey claveJwt) {
        return NimbusJwtDecoder
                .withSecretKey(claveJwt)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    // Toma el rol del claim "rol" y lo convierte en ROLE_<rol>.
    private JwtAuthenticationConverter convertidorJwt() {
        JwtGrantedAuthoritiesConverter autoridades =
                new JwtGrantedAuthoritiesConverter();
        autoridades.setAuthoritiesClaimName("rol");
        autoridades.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter convertidor =
                new JwtAuthenticationConverter();
        convertidor.setJwtGrantedAuthoritiesConverter(autoridades);
        return convertidor;
    }

    // API REST: sin sesión, cada pedido lleva el token en Authorization: Bearer.
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(
            HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sesion -> sesion
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .authorizeHttpRequests(autorizacion -> autorizacion
                        // Registro e inicio de sesión.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/usuarios",
                                "/api/auth/login"
                        )
                        .permitAll()
                        // Consulta pública de contenidos.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/cursos/**",
                                "/api/unidades/**",
                                "/api/lecciones/**",
                                "/api/ejercicios/**"
                        )
                        .permitAll()
                        // Administración de contenidos.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/cursos",
                                "/api/cursos/*/unidades",
                                "/api/unidades/*/lecciones",
                                "/api/lecciones/*/ejercicios",
                                "/api/ejercicios/*/retroalimentaciones",
                                "/api/evaluaciones/*/preguntas"
                        )
                        .hasRole("ADMINISTRADOR")
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/cursos/**",
                                "/api/unidades/**",
                                "/api/lecciones/**",
                                "/api/ejercicios/**"
                        )
                        .hasRole("ADMINISTRADOR")
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/cursos/**",
                                "/api/unidades/**",
                                "/api/lecciones/**",
                                "/api/ejercicios/**"
                        )
                        .hasRole("ADMINISTRADOR")
                        // Todo lo demás de la API necesita token válido.
                        .anyRequest()
                        .authenticated()
                )
                .oauth2ResourceServer(recurso -> recurso
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(
                                        convertidorJwt()
                                )
                        )
                );
        return http.build();
    }

    // Pantallas web (Thymeleaf): login por formulario y sesión, como antes.
    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(
            HttpSecurity http) throws Exception {
        http
                // CSRF protege los formularios web (Thymeleaf agrega el token solo).
                .userDetailsService(usuarioDetailsService)
                .authorizeHttpRequests(autorizacion -> autorizacion
                        .requestMatchers(
                                "/login",
                                "/error"
                        )
                        .permitAll()
                        // Pantallas que requieren usuario autenticado.
                        .requestMatchers(
                                "/web/ejercicios/**",
                                "/web/mi-progreso",
                                "/web/preferencias",
                                "/web/evaluaciones/**",
                                "/web/evaluacion"
                        )
                        .authenticated()
                        .requestMatchers(
                                "/registro",
                                "/registro-exitoso",
                                "/",
                                "/web/**",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        )
                        .permitAll()
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml"
                        )
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .formLogin(Customizer.withDefaults())
                .logout(Customizer.withDefaults());
        return http.build();
    }
}
