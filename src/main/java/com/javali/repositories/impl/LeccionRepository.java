package com.javali.repositories.impl;
import com.javali.models.Leccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeccionRepository extends JpaRepository <Leccion, Long> {
    List<Leccion> findByUnidadOrderByOrdenAsc(Long unidadId);   
}
