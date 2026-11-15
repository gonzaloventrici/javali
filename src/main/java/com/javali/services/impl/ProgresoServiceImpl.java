package com.javali.services.impl;

import com.javali.dtos.ProgresoResponse;
import com.javali.exceptions.UsuarioNoEncontradoException;
import com.javali.models.Ejercicio;
import com.javali.models.Leccion;
import com.javali.models.Progreso;
import com.javali.models.Usuario;
import com.javali.repositories.ProgresoRepository;
import com.javali.repositories.UsuarioRepository;
import com.javali.services.ProgresoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProgresoServiceImpl implements ProgresoService {

    private final ProgresoRepository progresoRepository;
    private final UsuarioRepository usuarioRepository;

    public ProgresoServiceImpl(
            ProgresoRepository progresoRepository,
            UsuarioRepository usuarioRepository) {

        this.progresoRepository = progresoRepository;
        this.usuarioRepository = usuarioRepository;
    }
    // Recibe los repositorios mediante inyección por constructor.

    @Override
    @Transactional
    public void actualizar(
            Usuario usuario,
            Ejercicio ejercicio,
            boolean correcta) {

        Leccion leccion = ejercicio.getLeccion();

        Progreso progreso = progresoRepository
                .findByUsuario_IdAndLeccion_Id(
                        usuario.getId(),
                        leccion.getId()
                )
                .orElseGet(() -> crearProgreso(usuario, leccion));

        progreso.setCantidadIntentos(
                progreso.getCantidadIntentos() + 1
        );

        if (correcta) {
            progreso.setRespuestasCorrectas(
                    progreso.getRespuestasCorrectas() + 1
            );
        }

        double porcentaje = calcularPorcentaje(
                progreso.getRespuestasCorrectas(),
                progreso.getCantidadIntentos()
        );

        progreso.setPorcentajeAciertos(porcentaje);
        progreso.setCompletada(porcentaje >= 80);
        progreso.setUltimaActividad(LocalDateTime.now());

        progresoRepository.save(progreso);
    }
    // Actualiza intentos, aciertos, porcentaje y estado de la lección.

    @Override
    @Transactional(readOnly = true)
    public List<ProgresoResponse> listarPorUsuario(Long usuarioId) {

        if (!usuarioRepository.existsById(usuarioId)) {
            throw new UsuarioNoEncontradoException(usuarioId);
        }

        return progresoRepository
                .findByUsuario_IdOrderByUltimaActividadDesc(usuarioId)
                .stream()
                .map(this::aRespuesta)
                .toList();
    }
    // Devuelve el progreso del usuario en todas sus lecciones.

    private Progreso crearProgreso(
            Usuario usuario,
            Leccion leccion) {

        Progreso progreso = new Progreso();

        progreso.setUsuario(usuario);
        progreso.setLeccion(leccion);
        progreso.setCantidadIntentos(0);
        progreso.setRespuestasCorrectas(0);
        progreso.setPorcentajeAciertos(0);
        progreso.setCompletada(false);
        progreso.setUltimaActividad(LocalDateTime.now());

        return progreso;
    }
    // Crea el progreso inicial de una lección.

    private double calcularPorcentaje(
            int correctas,
            int intentos) {

        if (intentos == 0) {
            return 0;
        }

        return correctas * 100.0 / intentos;
    }
    // Calcula el porcentaje de respuestas correctas.

    private ProgresoResponse aRespuesta(Progreso progreso) {

        return new ProgresoResponse(
                progreso.getId(),
                progreso.getUsuario().getId(),
                progreso.getLeccion().getId(),
                progreso.getLeccion().getTitulo(),
                progreso.getCantidadIntentos(),
                progreso.getRespuestasCorrectas(),
                progreso.getPorcentajeAciertos(),
                progreso.isCompletada(),
                progreso.getUltimaActividad()
        );
    }
    // Convierte el progreso en un DTO de respuesta.
}