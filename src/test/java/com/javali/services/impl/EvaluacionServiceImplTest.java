package com.javali.services.impl;

import com.javali.dtos.EvaluacionResponse;
import com.javali.dtos.FinalizarEvaluacionRequest;
import com.javali.dtos.RespuestaPreguntaDTO;
import com.javali.exceptions.RespuestaEvaluacionInvalidaException;
import com.javali.models.Evaluacion;
import com.javali.models.NivelCurso;
import com.javali.models.Pregunta;
import com.javali.models.Usuario;
import com.javali.repositories.EvaluacionRepository;
import com.javali.repositories.PreguntaRepository;
import com.javali.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluacionServiceImplTest {

    @Mock
    private EvaluacionRepository evaluacionRepository;

    @Mock
    private PreguntaRepository preguntaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private EvaluacionServiceImpl evaluacionService;

    @BeforeEach
    void preparar() {

        evaluacionService = new EvaluacionServiceImpl(
                evaluacionRepository,
                preguntaRepository,
                usuarioRepository
        );
    }// Crea el servicio usando repositorios simulados.

    @Test
    void finalizarConTodasCorrectasRecomiendaNivelAvanzado() {

        Long evaluacionId = 1L;

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setId(evaluacionId);
        evaluacion.setUsuario(usuario);
        evaluacion.setFinalizada(false);
        evaluacion.setNivelRecomendado(NivelCurso.INICIAL);

        Pregunta preguntaUno = crearPregunta(
                1L,
                evaluacion,
                1
        );

        Pregunta preguntaDos = crearPregunta(
                2L,
                evaluacion,
                2
        );

        FinalizarEvaluacionRequest request =
                new FinalizarEvaluacionRequest(
                        List.of(
                                new RespuestaPreguntaDTO(1L, 1),
                                new RespuestaPreguntaDTO(2L, 2)
                        )
                );

        when(evaluacionRepository.findById(evaluacionId))
                .thenReturn(Optional.of(evaluacion));
        when(preguntaRepository
                .findByEvaluacion_IdOrderByOrdenAsc(evaluacionId))
                .thenReturn(
                        List.of(preguntaUno, preguntaDos)
                );
        when(evaluacionRepository.save(evaluacion))
                .thenReturn(evaluacion);

        EvaluacionResponse resultado =
                evaluacionService.finalizar(
                        evaluacionId,
                        request
                );

        assertEquals(2, resultado.respuestasCorrectas());
        assertEquals(2, resultado.totalPreguntas());
        assertEquals(100.0, resultado.porcentaje());
        assertEquals(
                NivelCurso.AVANZADO,
                resultado.nivelRecomendado()
        );
        assertEquals(true, resultado.finalizada());
    } // Comprueba que un resultado del 100% recomiende nivel avanzado.

    @Test
    void finalizarConUnaCorrectaRecomiendaNivelIntermedio() {

        Long evaluacionId = 1L;

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setId(evaluacionId);
        evaluacion.setUsuario(usuario);
        evaluacion.setFinalizada(false);
        evaluacion.setNivelRecomendado(NivelCurso.INICIAL);

        Pregunta preguntaUno = crearPregunta(
                1L,
                evaluacion,
                1
        );

        Pregunta preguntaDos = crearPregunta(
                2L,
                evaluacion,
                2
        );

        FinalizarEvaluacionRequest request =
                new FinalizarEvaluacionRequest(
                        List.of(
                                new RespuestaPreguntaDTO(1L, 1),
                                new RespuestaPreguntaDTO(2L, 0)
                        )
                );

        when(evaluacionRepository.findById(evaluacionId))
                .thenReturn(Optional.of(evaluacion));
        when(preguntaRepository
                .findByEvaluacion_IdOrderByOrdenAsc(evaluacionId))
                .thenReturn(
                        List.of(preguntaUno, preguntaDos)
                );
        when(evaluacionRepository.save(evaluacion))
                .thenReturn(evaluacion);

        EvaluacionResponse resultado =
                evaluacionService.finalizar(
                        evaluacionId,
                        request
                );

        assertEquals(1, resultado.respuestasCorrectas());
        assertEquals(2, resultado.totalPreguntas());
        assertEquals(50.0, resultado.porcentaje());
        assertEquals(
                NivelCurso.INTERMEDIO,
                resultado.nivelRecomendado()
        );
    } // Comprueba que un resultado del 50% recomiende nivel intermedio.

    @Test
    void finalizarEvaluacionYaFinalizadaLanzaExcepcion() {

        Long evaluacionId = 1L;

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setId(evaluacionId);
        evaluacion.setUsuario(usuario);
        evaluacion.setFinalizada(true);
        evaluacion.setNivelRecomendado(NivelCurso.AVANZADO);

        FinalizarEvaluacionRequest request =
                new FinalizarEvaluacionRequest(
                        List.of(
                                new RespuestaPreguntaDTO(1L, 1)
                        )
                );

        when(evaluacionRepository.findById(evaluacionId))
                .thenReturn(Optional.of(evaluacion));

        assertThrows(
                RespuestaEvaluacionInvalidaException.class,
                () -> evaluacionService.finalizar(
                        evaluacionId,
                        request
                )
        );
    } // Impide finalizar dos veces la misma evaluación.

    private Pregunta crearPregunta(
            Long id,
            Evaluacion evaluacion,
            Integer indiceCorrecto) {

        Pregunta pregunta = new Pregunta();

        pregunta.setId(id);
        pregunta.setEvaluacion(evaluacion);
        pregunta.setEnunciado("Pregunta de prueba");
        pregunta.setOrden(id.intValue());
        pregunta.setOpciones(
                List.of(
                        "Opción 0",
                        "Opción 1",
                        "Opción 2"
                )
        );
        pregunta.setIndiceCorrecto(indiceCorrecto);

        return pregunta;
    } // Crea una pregunta mínima reutilizable para las pruebas.
}