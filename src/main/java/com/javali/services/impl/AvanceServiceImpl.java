package com.javali.services.impl;

import com.javali.dtos.EstadoAvance;
import com.javali.dtos.EstadoCursoResponse;
import com.javali.dtos.EstadoLeccionResponse;
import com.javali.dtos.EstadoUnidadResponse;
import com.javali.exceptions.CursoNoEncontradoException;
import com.javali.exceptions.LeccionNoEncontradaException;
import com.javali.exceptions.UnidadNoEncontradaException;
import com.javali.exceptions.UsuarioNoEncontradoException;
import com.javali.models.Curso;
import com.javali.models.Leccion;
import com.javali.models.Progreso;
import com.javali.models.RolUsuario;
import com.javali.models.Unidad;
import com.javali.models.Usuario;
import com.javali.repositories.CursoRepository;
import com.javali.repositories.LeccionRepository;
import com.javali.repositories.ProgresoRepository;
import com.javali.repositories.UnidadRepository;
import com.javali.repositories.UsuarioRepository;
import com.javali.services.AvanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AvanceServiceImpl implements AvanceService {

    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;
    private final UnidadRepository unidadRepository;
    private final LeccionRepository leccionRepository;
    private final ProgresoRepository progresoRepository;

    public AvanceServiceImpl(
            UsuarioRepository usuarioRepository,
            CursoRepository cursoRepository,
            UnidadRepository unidadRepository,
            LeccionRepository leccionRepository,
            ProgresoRepository progresoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.cursoRepository = cursoRepository;
        this.unidadRepository = unidadRepository;
        this.leccionRepository = leccionRepository;
        this.progresoRepository = progresoRepository;
    }

    // Ruta de un curso ya calculada para un usuario.
    private record Ruta(
            Map<Long, EstadoUnidadResponse> unidades,
            Map<Long, Map<Long, EstadoLeccionResponse>> leccionesPorUnidad) {}

    @Override
    @Transactional(readOnly = true)
    public Map<Long, EstadoCursoResponse> estadoCursos(Long usuarioId) {
        Usuario usuario = buscarUsuario(usuarioId);
        Map<Long, Progreso> progresos = progresosPorLeccion(usuarioId);
        Map<Long, EstadoCursoResponse> estados = new LinkedHashMap<>();
        for (Curso curso : cursoRepository.findAll()) {
            estados.put(
                    curso.getId(),
                    resumirCurso(
                            curso.getId(),
                            construirRuta(curso.getId(), progresos, usuario)
                    )
            );
        }
        return estados;
    }

    @Override
    @Transactional(readOnly = true)
    public EstadoCursoResponse estadoCurso(Long usuarioId, Long cursoId) {
        return resumirCurso(cursoId, calcularRuta(usuarioId, cursoId));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, EstadoUnidadResponse> estadoUnidades(
            Long usuarioId,
            Long cursoId) {
        return calcularRuta(usuarioId, cursoId).unidades();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, EstadoLeccionResponse> estadoLecciones(
            Long usuarioId,
            Long unidadId) {
        Unidad unidad = unidadRepository.findById(unidadId)
                .orElseThrow(() -> new UnidadNoEncontradaException(unidadId));
        Ruta ruta = calcularRuta(usuarioId, unidad.getCurso().getId());
        return ruta.leccionesPorUnidad().getOrDefault(unidadId, Map.of());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean leccionDesbloqueada(Long usuarioId, Long leccionId) {
        Leccion leccion = leccionRepository.findById(leccionId)
                .orElseThrow(() -> new LeccionNoEncontradaException(leccionId));
        Unidad unidad = leccion.getUnidadId();
        Ruta ruta = calcularRuta(usuarioId, unidad.getCurso().getId());
        EstadoLeccionResponse estado = ruta.leccionesPorUnidad()
                .getOrDefault(unidad.getId(), Map.of())
                .get(leccionId);
        return estado != null && estado.desbloqueada();
    }

    private Ruta calcularRuta(Long usuarioId, Long cursoId) {
        Usuario usuario = buscarUsuario(usuarioId);
        if (!cursoRepository.existsById(cursoId)) {
            throw new CursoNoEncontradoException(cursoId);
        }
        return construirRuta(cursoId, progresosPorLeccion(usuarioId), usuario);
    }

    // Recorre las lecciones del curso en orden: cada una se abre al completar la anterior.
    private Ruta construirRuta(
            Long cursoId,
            Map<Long, Progreso> progresos,
            Usuario usuario) {
        boolean sinBloqueos = usuario.getRol() == RolUsuario.ADMINISTRADOR;
        Map<Long, EstadoUnidadResponse> unidades = new LinkedHashMap<>();
        Map<Long, Map<Long, EstadoLeccionResponse>> leccionesPorUnidad =
                new LinkedHashMap<>();
        boolean anteriorCompletada = true;

        for (Unidad unidad
                : unidadRepository.findByCurso_IdOrderByOrdenAsc(cursoId)) {
            List<Leccion> lecciones = leccionRepository
                    .findByUnidad_IdOrderByOrdenAsc(unidad.getId());
            if (lecciones.isEmpty()) {
                unidades.put(
                        unidad.getId(),
                        new EstadoUnidadResponse(
                                unidad.getId(), EstadoAvance.PROXIMAMENTE, 0, 0)
                );
                continue;
            }

            Map<Long, EstadoLeccionResponse> estados = new LinkedHashMap<>();
            int completadas = 0;
            boolean primeraDesbloqueada = false;
            boolean primera = true;
            for (Leccion leccion : lecciones) {
                Progreso progreso = progresos.get(leccion.getId());
                boolean completada =
                        progreso != null && progreso.isCompletada();
                double porcentaje = progreso == null
                        ? 0.0
                        : progreso.getPorcentajeAciertos();
                boolean desbloqueada = sinBloqueos || anteriorCompletada;
                if (primera) {
                    primeraDesbloqueada = desbloqueada;
                    primera = false;
                }
                estados.put(
                        leccion.getId(),
                        new EstadoLeccionResponse(
                                leccion.getId(),
                                leccion.getTitulo(),
                                desbloqueada,
                                completada,
                                porcentaje
                        )
                );
                if (completada) {
                    completadas++;
                }
                anteriorCompletada = completada;
            }

            EstadoAvance estado;
            if (completadas == lecciones.size()) {
                estado = EstadoAvance.COMPLETADA;
            } else if (primeraDesbloqueada) {
                estado = EstadoAvance.EN_CURSO;
            } else {
                estado = EstadoAvance.BLOQUEADA;
            }
            unidades.put(
                    unidad.getId(),
                    new EstadoUnidadResponse(
                            unidad.getId(), estado, lecciones.size(), completadas)
            );
            leccionesPorUnidad.put(unidad.getId(), estados);
        }
        return new Ruta(unidades, leccionesPorUnidad);
    }

    private EstadoCursoResponse resumirCurso(Long cursoId, Ruta ruta) {
        int unidadesTotales = 0;
        int unidadesCompletadas = 0;
        int leccionesTotales = 0;
        int leccionesCompletadas = 0;
        for (EstadoUnidadResponse unidad : ruta.unidades().values()) {
            if (unidad.estado() == EstadoAvance.PROXIMAMENTE) {
                continue;
            }
            unidadesTotales++;
            if (unidad.estado() == EstadoAvance.COMPLETADA) {
                unidadesCompletadas++;
            }
            leccionesTotales += unidad.leccionesTotales();
            leccionesCompletadas += unidad.leccionesCompletadas();
        }
        boolean completado =
                leccionesTotales > 0
                        && leccionesCompletadas == leccionesTotales;
        return new EstadoCursoResponse(
                cursoId,
                completado,
                unidadesTotales,
                unidadesCompletadas,
                leccionesTotales,
                leccionesCompletadas
        );
    }

    private Map<Long, Progreso> progresosPorLeccion(Long usuarioId) {
        Map<Long, Progreso> mapa = new HashMap<>();
        for (Progreso progreso : progresoRepository
                .findByUsuario_IdOrderByUltimaActividadDesc(usuarioId)) {
            mapa.put(progreso.getLeccion().getId(), progreso);
        }
        return mapa;
    }

    private Usuario buscarUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(usuarioId));
    }
}
