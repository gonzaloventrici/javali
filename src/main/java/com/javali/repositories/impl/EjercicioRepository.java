package com.javali.repositories.impl;

import org.springframework.data.jpa.repository.JpaRepository;
import com.javali.models.Ejercicio;

import java.util.List;

public interface EjercicioRepository extends JpaRepository<Ejercicio, Long>{
    List<Ejercicio>findByLeccionIdOrderByOrdenAsc(Long leccionId);
}
