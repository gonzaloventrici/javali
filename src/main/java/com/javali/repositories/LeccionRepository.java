package com.javali.repositories;

import com.javali.models.Leccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeccionRepository extends JpaRepository<Leccion, Long> {
    List<Leccion> findByUnidad_IdOrderByOrdenAsc(Long unidadId);
}
// Busca lecciones usando el id de la unidad relacionada.