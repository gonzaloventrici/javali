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
import com.javali.dtos.ResumenProgresoResponse;
import com.javali.models.CategoriaError;
import com.javali.models.Intento;
import com.javali.repositories.IntentoRepository;
import com.javali.dtos.EstadoLeccionResponse;
import com.javali.exceptions.LeccionNoEncontradaException;
import com.javali.repositories.LeccionRepository;

import java.util.Optional;
import java.util.EnumMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;


@Service
public class ProgresoServiceImpl implements ProgresoService {

    private final ProgresoRepository progresoRepository;
    private final UsuarioRepository usuarioRepository;
    private final IntentoRepository intentoRepository;
    private final LeccionRepository leccionRepository;

// Guarda los repositorios necesarios para calcular el progreso.

    public ProgresoServiceImpl(
        ProgresoRepository progresoRepository,
        UsuarioRepository usuarioRepository,
        IntentoRepository intentoRepository,
        LeccionRepository leccionRepository) {

        this.progresoRepository = progresoRepository;
        this.usuarioRepository = usuarioRepository;
        this.intentoRepository = intentoRepository;
        this.leccionRepository = leccionRepository;
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

    @Override
    @Transactional(readOnly = true)
    public ResumenProgresoResponse obtenerResumen(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new UsuarioNoEncontradoException(usuarioId);
        }
        List<Intento> intentos =
                intentoRepository.findByUsuario_Id(usuarioId);

        List<Progreso> progresos =
                progresoRepository
                        .findByUsuario_IdOrderByUltimaActividadDesc(
                                usuarioId
                        );

        int totalIntentos = intentos.size();

        int respuestasCorrectas = 0;
        for (Intento intento : intentos) {
            if (intento.isCorrecta()) {
                respuestasCorrectas++;
            }
        } // Cuenta manualmente los intentos respondidos correctamente.

        double porcentajeGeneral = calcularPorcentaje(
                respuestasCorrectas,
                totalIntentos
        );

        CategoriaError errorFrecuente =
                calcularErrorFrecuente(intentos);

        List<String> fortalezas = new ArrayList<>();
        List<String> temasPendientes = new ArrayList<>();

        for (Progreso progreso : progresos) {
            String tituloLeccion =
                    progreso.getLeccion().getTitulo();
            if (progreso.isCompletada()) {
                fortalezas.add(tituloLeccion);
            } else {
                temasPendientes.add(tituloLeccion);
            }
        } // Separa las lecciones completadas de las que necesitan refuerzo.

        String recomendacion = crearRecomendacion(
                totalIntentos,
                porcentajeGeneral,
                errorFrecuente
        );

        return new ResumenProgresoResponse(
                usuarioId,
                totalIntentos,
                respuestasCorrectas,
                porcentajeGeneral,
                errorFrecuente,
                fortalezas,
                temasPendientes,
                recomendacion
        );
    }
    // Calcula el desempeño general, fortalezas y temas pendientes.

    @Override
    @Transactional(readOnly = true)
    public EstadoLeccionResponse obtenerEstadoLeccion(
            Long usuarioId,
            Long leccionId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new UsuarioNoEncontradoException(usuarioId);
        }
        Leccion leccion = leccionRepository.findById(leccionId)
                .orElseThrow(
                        () -> new LeccionNoEncontradaException(leccionId)
                );
        Optional<Progreso> progresoActual =
                progresoRepository.findByUsuario_IdAndLeccion_Id(
                        usuarioId,
                        leccionId
                );
        boolean completada = false;
        double porcentaje = 0.0;
        if (progresoActual.isPresent()) {
            Progreso progreso = progresoActual.get();

            completada = progreso.isCompletada();
            porcentaje = progreso.getPorcentajeAciertos();
        } // Obtiene el estado actual de la lección si existe un progreso.

        Optional<Leccion> leccionAnterior =
                leccionRepository
                        .findFirstByUnidad_IdAndOrdenLessThanOrderByOrdenDesc(
                                leccion.getUnidadId().getId(),
                                leccion.getOrden()
                        );
        boolean desbloqueada;
        if (leccionAnterior.isEmpty()) {
            desbloqueada = true;
        } else {
            Leccion anterior = leccionAnterior.get();
            Optional<Progreso> progresoAnterior =
                    progresoRepository
                            .findByUsuario_IdAndLeccion_Id(
                                    usuarioId,
                                    anterior.getId()
                            );
            desbloqueada = progresoAnterior.isPresent()
                    && progresoAnterior.get().isCompletada();
        } // Desbloquea la primera lección o exige completar la anterior.
        return new EstadoLeccionResponse(
                leccion.getId(),
                leccion.getTitulo(),
                desbloqueada,
                completada,
                porcentaje
        );
    }
    // Devuelve el avance y el estado de desbloqueo de una lección.
    
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

    //AUXILIARES
    private CategoriaError calcularErrorFrecuente(
        List<Intento> intentos) {
        Map<CategoriaError, Integer> cantidades =
                new EnumMap<>(CategoriaError.class);
        for (Intento intento : intentos) {
            CategoriaError categoria =
                    intento.getCategoriaError();
            if (categoria != null) {
                Integer cantidadActual =
                        cantidades.get(categoria);
                if (cantidadActual == null) {
                    cantidades.put(categoria, 1);
                } else {
                    cantidades.put(
                            categoria,
                            cantidadActual + 1
                        );
                    }
                }
            }
        CategoriaError errorFrecuente = null;
        int mayorCantidad = 0;
        for (Map.Entry<CategoriaError, Integer> entrada: cantidades.entrySet()) {
            Integer cantidad = entrada.getValue();
            if (cantidad != null && cantidad > mayorCantidad) {
                errorFrecuente = entrada.getKey();
                mayorCantidad = cantidad;
            }
        } // Compara las cantidades evitando utilizar un valor nulo.
        
        return errorFrecuente;
    }
    // Busca explícitamente la categoría de error más repetida.

    private String crearRecomendacion(
        int totalIntentos,
        double porcentajeGeneral,
        CategoriaError errorFrecuente) {

        if (totalIntentos == 0) {
            return "Realizar ejercicios para comenzar a medir el progreso.";
        }

        if (porcentajeGeneral >= 80) {
            return "El desempeño es suficiente para continuar con nuevos contenidos.";
        }

        if (errorFrecuente == null) {
            return "Continuar practicando los temas pendientes.";
        }

        return switch (errorFrecuente) {
            case SINTAXIS ->
                    "Practicar la escritura y estructura de código Java.";
            case COMPILACION ->
                    "Revisar tipos de datos, variables y mensajes de compilación.";
            case LOGICA ->
                    "Practicar el seguimiento paso a paso de los algoritmos.";
            case CONCEPTO ->
                    "Repasar las explicaciones y ejemplos de los temas pendientes.";
        };
    }
    // Genera una recomendación según el desempeño y el error frecuente.
}