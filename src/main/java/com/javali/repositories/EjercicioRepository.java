package com.javali.repositories;

import com.javali.models.Ejercicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EjercicioRepository
        extends JpaRepository<Ejercicio, Long> {
    List<Ejercicio> findByLeccionIdOrderByOrdenAsc(Long leccionId);
}
// Busca los ejercicios de una lección y los ordena por orden.