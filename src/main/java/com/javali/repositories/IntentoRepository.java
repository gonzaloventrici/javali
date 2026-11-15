package com.javali.repositories;

import com.javali.models.Intento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IntentoRepository extends JpaRepository<Intento, Long> {

    List<Intento> findByUsuario_IdOrderByFechaDesc(Long usuarioId);
    // Devuelve todos los intentos del usuario, del más reciente al más antiguo.

    List<Intento> findByUsuario_IdAndEjercicio_IdOrderByFechaDesc(
            Long usuarioId,
            Long ejercicioId
    );
    // Devuelve los intentos del usuario para un ejercicio específico.
}
// Permite guardar intentos y consultar el historial de un usuario.