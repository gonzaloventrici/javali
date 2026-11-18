package com.javali.services.impl;

import com.javali.dtos.RetroalimentacionRequest;
import com.javali.dtos.RetroalimentacionResponse;
import com.javali.exceptions.EjercicioNoEncontradoException;
import com.javali.models.Ejercicio;
import com.javali.models.RetroalimentacionOpcion;
import com.javali.repositories.EjercicioRepository;
import com.javali.repositories.RetroalimentacionOpcionRepository;
import com.javali.services.RetroalimentacionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RetroalimentacionServiceImpl
        implements RetroalimentacionService {

    private final RetroalimentacionOpcionRepository retroalimentacionRepository;
    private final EjercicioRepository ejercicioRepository;

    public RetroalimentacionServiceImpl(
            RetroalimentacionOpcionRepository retroalimentacionRepository,
            EjercicioRepository ejercicioRepository) {

        this.retroalimentacionRepository = retroalimentacionRepository;
        this.ejercicioRepository = ejercicioRepository;
    }
    // Recibe los repositorios mediante inyección por constructor.

    @Override
    @Transactional
    public RetroalimentacionResponse crear(
            Long ejercicioId,
            RetroalimentacionRequest request) {

        Ejercicio ejercicio = ejercicioRepository.findById(ejercicioId)
                .orElseThrow(
                        () -> new EjercicioNoEncontradoException(ejercicioId)
                );

        RetroalimentacionOpcion retroalimentacion =
                new RetroalimentacionOpcion();

        retroalimentacion.setEjercicio(ejercicio);
        retroalimentacion.setIndiceOpcion(request.indiceOpcion());
        retroalimentacion.setCategoriaError(request.categoriaError());
        retroalimentacion.setExplicacion(request.explicacion());
        retroalimentacion.setPista(request.pista());

        RetroalimentacionOpcion guardada =
                retroalimentacionRepository.save(retroalimentacion);

        return new RetroalimentacionResponse(
                guardada.getId(),
                guardada.getEjercicio().getId(),
                guardada.getIndiceOpcion(),
                guardada.getCategoriaError(),
                guardada.getExplicacion(),
                guardada.getPista()
        );
    }
    // Guarda la retroalimentación asociada a un ejercicio y una opción.
}