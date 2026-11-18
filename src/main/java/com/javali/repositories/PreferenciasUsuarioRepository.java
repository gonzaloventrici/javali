package com.javali.repositories;

import com.javali.models.PreferenciasUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreferenciasUsuarioRepository
        extends JpaRepository<PreferenciasUsuario, Long> {

    Optional<PreferenciasUsuario> findByUsuario_Id(Long usuarioId);
    // Busca los ajustes guardados por un usuario.
}
