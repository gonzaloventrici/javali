package com.javali.config;

import com.javali.security.UsuarioDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) //para seguir probando la API con Postman.
                .userDetailsService(usuarioDetailsService)
                .authorizeHttpRequests(autorizacion ->autorizacion
                // Registro y pantalla de ingreso.
                .requestMatchers(
                                HttpMethod.POST,
                                "/api/usuarios"
                                )
                .permitAll()
                .requestMatchers(
                                "/login",
                                "/error"
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
                // Operaciones del estudiante.
                .requestMatchers(
                                HttpMethod.POST,
                                "/api/ejercicios/*/respuestas",
                                "/api/usuarios/*/evaluaciones",
                                "/api/evaluaciones/*/finalizacion"
                                )
                .authenticated()
                .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/evaluaciones/*/nivel"
                                )
                .authenticated()
                // Intentos, progreso y evaluaciones personales.
                .requestMatchers(
                                "/api/usuarios/*/intentos",
                                "/api/usuarios/*/progresos",
                                "/api/usuarios/*/progreso/**",
                                "/api/usuarios/*/lecciones/*/estado",
                                "/api/usuarios/*/evaluaciones",
                                "/web/ejercicios/**",
                                "/web/mi-progreso",
                                "/web/evaluaciones/**",
                                "/web/evaluacion"
                                )
                .authenticated()
                // Todo lo demás necesita autenticación.
                .requestMatchers(
                                "/login",
                                "/registro",
                                "/error",
                                "/",
                                "/web/**",
                                "/css/**",
                                "/images/**"
                                )
                .permitAll()
                .anyRequest()
                .authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults())
                .logout(Customizer.withDefaults());
        return http.build();
    }
}