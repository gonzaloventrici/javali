package com.javali.config;

import com.javali.models.RolUsuario;
import com.javali.models.Usuario;
import com.javali.repositories.UsuarioRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("demo")
public class DatosDemoConfig {

    @Bean
    ApplicationRunner cargarUsuariosDemo(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        return argumentos -> {

            Usuario administrador = new Usuario();
            administrador.setNombre("Administrador Javali");
            administrador.setEmail("vebes@javali.com");
            administrador.setPasswordHash(
                    passwordEncoder.encode("vebes123")
            );
            administrador.setRol(RolUsuario.ADMINISTRADOR);

            usuarioRepository.save(administrador);

            Usuario estudiante = new Usuario();
            estudiante.setNombre("Estudiante Demo");
            estudiante.setEmail("estudiante@javali.com");
            estudiante.setPasswordHash(
                    passwordEncoder.encode("Estudiante123")
            );
            estudiante.setRol(RolUsuario.ESTUDIANTE);

            usuarioRepository.save(estudiante);

            System.out.println(
                    "Usuarios demo cargados: admin y estudiante"
            );
        };
    }
}
