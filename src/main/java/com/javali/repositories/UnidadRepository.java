package com.javali.repositories;

import com.javali.models.Unidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnidadRepository extends JpaRepository<Unidad, Long> {
    List<Unidad> findAllByOrderByOrdenAsc();
    //Permite listar todas las unidades
    List<Unidad> findByCurso_IdOrderByOrdenAsc(Long cursoId);
    //Permite listar solamente las unidades de un curso
    boolean existsByCurso_Id(Long cursoId);
    // Comprueba si el curso tiene unidades asociadas.
    }

// JpaRepository<Unidad, Long> incluye gratis save, findById, findAll, deleteById 
// y más. Los parámetros son la entidad y el tipo de su id.
// findByUnidadIdOrderByOrdenAsc se lee como “buscar lecciones de la unidad con 
// ese id, ordenadas por orden”. Spring arma el SQL a partir del nombre.