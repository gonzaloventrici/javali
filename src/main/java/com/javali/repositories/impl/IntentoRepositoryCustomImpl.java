package com.javali.repositories.impl;

import com.javali.repositories.IntentoRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// Implementación personalizada: consulta solo las fechas, sin cargar intentos completos.
public class IntentoRepositoryCustomImpl implements IntentoRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<LocalDate> listarDiasConIntentos(Long usuarioId) {
        List<LocalDateTime> fechas = entityManager
                .createQuery(
                        "select i.fecha from Intento i "
                                + "where i.usuario.id = :usuarioId",
                        LocalDateTime.class
                )
                .setParameter("usuarioId", usuarioId)
                .getResultList();
        return fechas.stream()
                .map(fecha -> fecha.toLocalDate())
                .distinct()
                .sorted()
                .toList();
    }
}