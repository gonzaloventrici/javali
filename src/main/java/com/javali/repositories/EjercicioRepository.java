package com.javali.repositories;

import com.javali.models.Ejercicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {
    List<Ejercicio> findByLeccion_IdOrderByOrdenAsc(Long leccionId);

    boolean existsByLeccion_Id(Long leccionId);
    // Comprueba si una lección contiene ejercicios.
}
// Busca ejercicios usando el id de la lección relacionada.