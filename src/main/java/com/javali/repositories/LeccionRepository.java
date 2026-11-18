package com.javali.repositories;

import com.javali.models.Leccion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import java.util.List;

public interface LeccionRepository extends JpaRepository<Leccion, Long> {
    List<Leccion> findByUnidad_IdOrderByOrdenAsc(Long unidadId);
    // Permite listar lecciones

    Optional<Leccion> findFirstByUnidad_IdAndOrdenLessThanOrderByOrdenDesc(
        Long unidadId,
        int orden
    ); // Busca la lección anterior dentro de la misma unidad.

    boolean existsByUnidad_Id(Long unidadId);
// Comprueba si una unidad contiene lecciones.
}
// Busca lecciones usando el id de la unidad relacionada.