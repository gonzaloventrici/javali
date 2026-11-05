package com.javali.services.impl;

import com.javali.dtos.EjercicioRequest;
import com.javali.dtos.EjercicioResponse;
import com.javali.exceptions.EjercicioNoEncontradoException;
import com.javali.exceptions.LeccionNoEncontradaException;
import com.javali.models.Ejercicio;
import com.javali.models.Leccion;
import com.javali.repositories.EjercicioRepository;
import com.javali.repositories.LeccionRepository;
import com.javali.services.EjercicioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.javali.dtos.RespuestaDTO;
import com.javali.dtos.ResultadoCorreccionDTO;

import java.util.Objects;
import java.util.List;

@Service
public class EjercicioServiceImpl implements EjercicioService {

    private final EjercicioRepository ejercicioRepository;
    private final LeccionRepository leccionRepository;

    public EjercicioServiceImpl(
            EjercicioRepository ejercicioRepository,
            LeccionRepository leccionRepository) {
        this.ejercicioRepository = ejercicioRepository;
        this.leccionRepository = leccionRepository;
    }
    // Recibe los repositorios mediante inyección por constructor.

    @Override
    @Transactional(readOnly = true)
    public List<EjercicioResponse> listarPorLeccion(Long leccionId) {
        if (!leccionRepository.existsById(leccionId)) {
            throw new LeccionNoEncontradaException(leccionId);
        }
        return ejercicioRepository
                .findByLeccionIdOrderByOrdenAsc(leccionId)
                .stream()
                .map(this::aRespuesta)
                .toList();
    }
    // Verifica la lección y devuelve sus ejercicios ordenados.

    @Override
    @Transactional(readOnly = true)
    public EjercicioResponse obtener(Long id) {
        Ejercicio ejercicio = ejercicioRepository.findById(id)
                .orElseThrow(
                        () -> new EjercicioNoEncontradoException(id)
                );
        return aRespuesta(ejercicio);
    }
    // Busca un ejercicio por id o lanza una excepción.

    @Override
    @Transactional
    public EjercicioResponse crear(
            Long leccionId,
            EjercicioRequest request) {
        Leccion leccion = leccionRepository.findById(leccionId)
                .orElseThrow(
                        () -> new LeccionNoEncontradaException(leccionId)
                );
        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setLeccion(leccion);
        ejercicio.setTipo(request.tipo());
        ejercicio.setEnunciado(request.enunciado());
        ejercicio.setCodigo(request.codigo());
        ejercicio.setExplicacion(request.explicacion());
        ejercicio.setPista(request.pista());
        ejercicio.setOrden(request.orden());
        ejercicio.setOpciones(request.opciones());
        ejercicio.setIndiceCorrecto(request.indiceCorrecto());
        return aRespuesta(ejercicioRepository.save(ejercicio));
    }
    // Crea un ejercicio relacionado con una lección existente.

    @Override
    @Transactional(readOnly = true)
    public ResultadoCorreccionDTO corregir(
            Long id,
            RespuestaDTO respuesta) {
        Ejercicio ejercicio = ejercicioRepository.findById(id)
                .orElseThrow(
                        () -> new EjercicioNoEncontradoException(id)
                );
        boolean correcta = Objects.equals(
                ejercicio.getIndiceCorrecto(),
                respuesta.indiceSeleccionado()
        );
        String pista = correcta ? null : ejercicio.getPista();
        return new ResultadoCorreccionDTO(
                correcta,
                ejercicio.getExplicacion(),
                pista
        );
    }
    // Compara la opción elegida con la correcta y devuelve el resultado.

    private EjercicioResponse aRespuesta(Ejercicio ejercicio) {
        return new EjercicioResponse(
                ejercicio.getId(),
                ejercicio.getLeccion().getId(),
                ejercicio.getTipo(),
                ejercicio.getEnunciado(),
                ejercicio.getCodigo(),
                ejercicio.getExplicacion(),
                ejercicio.getPista(),
                ejercicio.getOrden(),
                ejercicio.getOpciones()
        );
    }
    // Convierte la entidad Ejercicio en un DTO sin la respuesta correcta.
}