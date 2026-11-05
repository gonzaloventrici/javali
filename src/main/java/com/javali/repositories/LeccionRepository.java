package com.javali.repositories;

import com.javali.models.Leccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeccionRepository extends JpaRepository<Leccion, Long> {
    List<Leccion> findByUnidadIdOrderByOrdenAsc(Long unidadId);
}
// Busca las lecciones de una unidad y las ordena por el campo orden.