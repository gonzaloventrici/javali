package com.javali.services.impl;

import com.javali.dtos.LeccionRequest;
import com.javali.dtos.LeccionResponse;
import com.javali.exceptions.LeccionNoEncontradaException;
import com.javali.exceptions.UnidadNoEncontradaException;
import com.javali.models.Leccion;
import com.javali.models.Unidad;
import com.javali.repositories.LeccionRepository;
import com.javali.repositories.UnidadRepository;
import com.javali.services.LeccionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LeccionServiceImpl implements LeccionService {

    private final LeccionRepository leccionRepository;
    private final UnidadRepository unidadRepository;

    public LeccionServiceImpl(
            LeccionRepository leccionRepository,
            UnidadRepository unidadRepository) {

        this.leccionRepository = leccionRepository;
        this.unidadRepository = unidadRepository;
    }

    // Recibe los repositorios mediante inyección por constructor.

    @Override
    @Transactional(readOnly = true)
    public List<LeccionResponse> listarPorUnidad(Long unidadId) {

        if (!unidadRepository.existsById(unidadId)) {
            throw new UnidadNoEncontradaException(unidadId);
        }

        return leccionRepository
                .findByUnidadIdOrderByOrdenAsc(unidadId)
                .stream()
                .map(this::aRespuesta)
                .toList();
    }

    // Verifica la unidad y devuelve sus lecciones ordenadas.

    @Override
    @Transactional(readOnly = true)
    public LeccionResponse obtener(Long id) {

        Leccion leccion = leccionRepository.findById(id)
                .orElseThrow(() -> new LeccionNoEncontradaException(id));

        return aRespuesta(leccion);
    }

    // Busca una lección por id o lanza una excepción si no existe.

    @Override
    @Transactional
    public LeccionResponse crear(Long unidadId, LeccionRequest request) {

        Unidad unidad = unidadRepository.findById(unidadId)
                .orElseThrow(() -> new UnidadNoEncontradaException(unidadId));

        Leccion leccion = new Leccion();
        leccion.setUnidadId(unidad);
        leccion.setTitulo(request.titulo());
        leccion.setExplicacion(request.explicacion());
        leccion.setOrden(request.orden());

        return aRespuesta(leccionRepository.save(leccion));
    }

    // Crea una lección relacionada con una unidad existente.

    private LeccionResponse aRespuesta(Leccion leccion) {

        return new LeccionResponse(
                leccion.getId(),
                leccion.getUnidadId().getId(),
                leccion.getTitulo(),
                leccion.getExplicacion(),
                leccion.getOrden()
        );
    }

    // Convierte una entidad Leccion en un DTO de respuesta.
}