package com.javali.repositories;

import com.javali.models.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CursoRepository extends JpaRepository<Curso, Long> {

    List<Curso> findAllByOrderByTituloAsc();
}
// Permite guardar cursos y listarlos por título de manera ascendente.