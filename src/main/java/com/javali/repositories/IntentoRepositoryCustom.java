package com.javali.repositories;

import java.time.LocalDate;
import java.util.List;

public interface IntentoRepositoryCustom {

    List<LocalDate> listarDiasConIntentos(Long usuarioId);
    // Devuelve, sin repetir y en orden, los días en que el usuario practicó.
}
