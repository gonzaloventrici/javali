package com.javali.repositories;

import com.javali.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository
        extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    // Busca un usuario mediante su correo electrónico.
    boolean existsByEmail(String email);
    // Comprueba si ya existe un usuario con ese correo.
}
// Permite guardar usuarios y buscarlos por correo electrónico.