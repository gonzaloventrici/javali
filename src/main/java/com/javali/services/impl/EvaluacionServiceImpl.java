package com.javali.services.impl;

import com.javali.dtos.EvaluacionResponse;
import com.javali.dtos.FinalizarEvaluacionRequest;
import com.javali.dtos.NivelElegidoRequest;
import com.javali.dtos.PreguntaRequest;
import com.javali.dtos.PreguntaResponse;
import com.javali.dtos.RespuestaPreguntaDTO;
import com.javali.exceptions.EvaluacionNoEncontradaException;
import com.javali.exceptions.RespuestaEvaluacionInvalidaException;
import com.javali.exceptions.UsuarioNoEncontradoException;
import com.javali.models.Evaluacion;
import com.javali.models.NivelCurso;
import com.javali.models.Pregunta;
import com.javali.models.Usuario;
import com.javali.repositories.EvaluacionRepository;
import com.javali.repositories.PreguntaRepository;
import com.javali.repositories.UsuarioRepository;
import com.javali.services.EvaluacionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class EvaluacionServiceImpl implements EvaluacionService {

    private final EvaluacionRepository evaluacionRepository;
    private final PreguntaRepository preguntaRepository;
    private final UsuarioRepository usuarioRepository;

    public EvaluacionServiceImpl(
            EvaluacionRepository evaluacionRepository,
            PreguntaRepository preguntaRepository,
            UsuarioRepository usuarioRepository) {

        this.evaluacionRepository = evaluacionRepository;
        this.preguntaRepository = preguntaRepository;
        this.usuarioRepository = usuarioRepository;
    }
    // Recibe los repositorios mediante inyección por constructor.

    @Override
    @Transactional
    public EvaluacionResponse iniciar(Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(
                        () -> new UsuarioNoEncontradoException(usuarioId)
                );

        Evaluacion evaluacion = new Evaluacion();

        evaluacion.setUsuario(usuario);
        evaluacion.setRespuestasCorrectas(0);
        evaluacion.setTotalPreguntas(0);
        evaluacion.setPorcentaje(0);
        evaluacion.setNivelRecomendado(NivelCurso.INICIAL);
        //Esto evita guardar null porque la columna es obligatoria. 
        //El valor se reemplaza al finalizar la evaluación.
        evaluacion.setNivelElegido(null);
        evaluacion.setFinalizada(false);
        evaluacion.setFecha(LocalDateTime.now());

        return aRespuesta(evaluacionRepository.save(evaluacion));
    }
    // Crea una evaluación inicial pendiente para el usuario.

    @Override
    @Transactional
    public PreguntaResponse agregarPregunta(
            Long evaluacionId,
            PreguntaRequest request) {

        Evaluacion evaluacion = buscarEvaluacion(evaluacionId);

        if (evaluacion.isFinalizada()) {
            throw new RespuestaEvaluacionInvalidaException(
                    "No se pueden agregar preguntas a una evaluación finalizada"
            );
        }

        validarIndiceCorrecto(
                request.indiceCorrecto(),
                request.opciones().size()
        );

        Pregunta pregunta = new Pregunta();

        pregunta.setEvaluacion(evaluacion);
        pregunta.setEnunciado(request.enunciado());
        pregunta.setCodigo(request.codigo());
        pregunta.setOrden(request.orden());
        pregunta.setOpciones(new ArrayList<>(request.opciones()));
        pregunta.setIndiceCorrecto(request.indiceCorrecto());

        return preguntaARespuesta(
                preguntaRepository.save(pregunta)
        );
    }
    // Agrega una pregunta válida a una evaluación pendiente.

    @Override
    @Transactional(readOnly = true)
    public List<PreguntaResponse> listarPreguntas(
            Long evaluacionId) {

        buscarEvaluacion(evaluacionId);

        return preguntaRepository
                .findByEvaluacion_IdOrderByOrdenAsc(evaluacionId)
                .stream()
                .map(this::preguntaARespuesta)
                .toList();
    }
    // Devuelve las preguntas sin mostrar sus respuestas correctas.

    @Override
    @Transactional
    public EvaluacionResponse finalizar(
            Long evaluacionId,
            FinalizarEvaluacionRequest request) {

        Evaluacion evaluacion = buscarEvaluacion(evaluacionId);

        if (evaluacion.isFinalizada()) {
            throw new RespuestaEvaluacionInvalidaException(
                    "La evaluación ya fue finalizada"
            );
        }

        List<Pregunta> preguntas = preguntaRepository
                .findByEvaluacion_IdOrderByOrdenAsc(evaluacionId);

        if (preguntas.isEmpty()) {
            throw new RespuestaEvaluacionInvalidaException(
                    "La evaluación no tiene preguntas"
            );
        }

        validarCantidadRespuestas(
                preguntas.size(),
                request.respuestas().size()
        );

        Map<Long, RespuestaPreguntaDTO> respuestas =
                construirMapaRespuestas(request.respuestas());

        int correctas = 0;

        for (Pregunta pregunta : preguntas) {

            RespuestaPreguntaDTO respuesta =
                    respuestas.get(pregunta.getId());

            if (respuesta == null) {
                throw new RespuestaEvaluacionInvalidaException(
                        "Falta responder la pregunta " + pregunta.getId()
                );
            }

            validarIndiceSeleccionado(
                    respuesta.indiceSeleccionado(),
                    pregunta.getOpciones().size()
            );

            if (pregunta.getIndiceCorrecto()
                    .equals(respuesta.indiceSeleccionado())) {

                correctas++;
            }
        }

        double porcentaje =
                correctas * 100.0 / preguntas.size();

        evaluacion.setRespuestasCorrectas(correctas);
        evaluacion.setTotalPreguntas(preguntas.size());
        evaluacion.setPorcentaje(porcentaje);
        evaluacion.setNivelRecomendado(
                calcularNivel(porcentaje)
        );
        evaluacion.setFinalizada(true);
        evaluacion.setFecha(LocalDateTime.now());

        return aRespuesta(evaluacionRepository.save(evaluacion));
    }
    // Corrige las respuestas y calcula el nivel recomendado.

    @Override
    @Transactional
    public EvaluacionResponse elegirNivel(
            Long evaluacionId,
            NivelElegidoRequest request) {

        Evaluacion evaluacion = buscarEvaluacion(evaluacionId);

        if (!evaluacion.isFinalizada()) {
            throw new RespuestaEvaluacionInvalidaException(
                    "Primero debe finalizar la evaluación"
            );
        }

        evaluacion.setNivelElegido(request.nivel());

        return aRespuesta(evaluacionRepository.save(evaluacion));
    }
    // Guarda el nivel que el estudiante decide utilizar.

    @Override
    @Transactional(readOnly = true)
    public List<EvaluacionResponse> listarPorUsuario(
            Long usuarioId) {

        if (!usuarioRepository.existsById(usuarioId)) {
            throw new UsuarioNoEncontradoException(usuarioId);
        }

        return evaluacionRepository
                .findByUsuario_IdOrderByFechaDesc(usuarioId)
                .stream()
                .map(this::aRespuesta)
                .toList();
    }
    // Devuelve las evaluaciones del usuario por fecha descendente.

    private Evaluacion buscarEvaluacion(Long evaluacionId) {

        return evaluacionRepository.findById(evaluacionId)
                .orElseThrow(
                        () -> new EvaluacionNoEncontradaException(
                                evaluacionId
                        )
                );
    }
    // Busca una evaluación o informa que no existe.

    private void validarIndiceCorrecto(
            Integer indiceCorrecto,
            int cantidadOpciones) {

        if (indiceCorrecto >= cantidadOpciones) {
            throw new RespuestaEvaluacionInvalidaException(
                    "El índice correcto está fuera de las opciones"
            );
        }
    }
    // Comprueba que la respuesta correcta exista en las opciones.

    private void validarIndiceSeleccionado(
            Integer indiceSeleccionado,
            int cantidadOpciones) {

        if (indiceSeleccionado >= cantidadOpciones) {
            throw new RespuestaEvaluacionInvalidaException(
                    "El índice seleccionado está fuera de las opciones"
            );
        }
    }
    // Comprueba que la opción elegida exista en la pregunta.

    private void validarCantidadRespuestas(
            int cantidadPreguntas,
            int cantidadRespuestas) {

        if (cantidadPreguntas != cantidadRespuestas) {
            throw new RespuestaEvaluacionInvalidaException(
                    "Debe responder todas las preguntas una sola vez"
            );
        }
    }
    // Comprueba que se responda la evaluación completa.

    private Map<Long, RespuestaPreguntaDTO> construirMapaRespuestas(
            List<RespuestaPreguntaDTO> respuestas) {

        Map<Long, RespuestaPreguntaDTO> mapa = new HashMap<>();
        Set<Long> identificadores = new HashSet<>();

        for (RespuestaPreguntaDTO respuesta : respuestas) {

            if (!identificadores.add(respuesta.preguntaId())) {
                throw new RespuestaEvaluacionInvalidaException(
                        "No se puede responder dos veces la misma pregunta"
                );
            }

            mapa.put(respuesta.preguntaId(), respuesta);
        }

        return mapa;
    }
    // Organiza las respuestas y rechaza preguntas duplicadas.

    private NivelCurso calcularNivel(double porcentaje) {

        if (porcentaje < 40) {
            return NivelCurso.INICIAL;
        }

        if (porcentaje < 70) {
            return NivelCurso.INTERMEDIO;
        }

        return NivelCurso.AVANZADO;
    }
    // Recomienda un nivel según el porcentaje obtenido.

    private PreguntaResponse preguntaARespuesta(
            Pregunta pregunta) {

        return new PreguntaResponse(
                pregunta.getId(),
                pregunta.getEvaluacion().getId(),
                pregunta.getEnunciado(),
                pregunta.getCodigo(),
                pregunta.getOrden(),
                List.copyOf(pregunta.getOpciones())
        );
    }
    // Convierte la pregunta en DTO sin mostrar la respuesta correcta.

    private EvaluacionResponse aRespuesta(
            Evaluacion evaluacion) {

        return new EvaluacionResponse(
                evaluacion.getId(),
                evaluacion.getUsuario().getId(),
                evaluacion.getRespuestasCorrectas(),
                evaluacion.getTotalPreguntas(),
                evaluacion.getPorcentaje(),
                evaluacion.getNivelRecomendado(),
                evaluacion.getNivelElegido(),
                evaluacion.isFinalizada(),
                evaluacion.getFecha()
        );
    }
    // Convierte la evaluación en un DTO de respuesta.
}