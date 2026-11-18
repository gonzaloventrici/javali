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

    // ---------- Puntaje ponderado por dificultad ----------

    private EvaluacionResponse finalizarConResultados(
            List<Pregunta> preguntas,
            java.util.Set<Long> correctas) {

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setId(1L);
        evaluacion.setUsuario(usuario);
        evaluacion.setFinalizada(false);
        evaluacion.setNivelRecomendado(NivelCurso.INICIAL);

        List<RespuestaPreguntaDTO> respuestas = preguntas.stream()
                .map(pregunta -> new RespuestaPreguntaDTO(
                        pregunta.getId(),
                        correctas.contains(pregunta.getId()) ? 1 : 0))
                .toList();

        when(evaluacionRepository.findById(1L))
                .thenReturn(Optional.of(evaluacion));
        when(preguntaRepository.findByEvaluacion_IdOrderByOrdenAsc(1L))
                .thenReturn(preguntas);
        when(evaluacionRepository.save(evaluacion))
                .thenReturn(evaluacion);

        return evaluacionService.finalizar(
                1L,
                new FinalizarEvaluacionRequest(respuestas)
        );
    } // Responde cada pregunta bien (opción 1) o mal (opción 0).

    private Pregunta crearPreguntaConDificultad(Long id, int dificultad) {
        Pregunta pregunta = crearPregunta(id, null, 1);
        pregunta.setDificultad(dificultad);
        return pregunta;
    } // Pregunta cuya respuesta correcta es la opción 1.

    private List<Pregunta> bancoDeOchoPreguntas() {
        java.util.List<Pregunta> preguntas = new java.util.ArrayList<>();
        preguntas.add(crearPreguntaConDificultad(1L, 1));
        for (long id = 2; id <= 7; id++) {
            preguntas.add(crearPreguntaConDificultad(id, 2));
        }
        preguntas.add(crearPreguntaConDificultad(8L, 3));
        return preguntas;
    } // 1 básica, 6 intermedias y 1 avanzada: 16 puntos en total.

    @Test
    void lasPreguntasDificilesPesanMasEnElPuntaje() {
        EvaluacionResponse resultado = finalizarConResultados(
                List.of(
                        crearPreguntaConDificultad(1L, 1),
                        crearPreguntaConDificultad(2L, 2),
                        crearPreguntaConDificultad(3L, 3)),
                java.util.Set.of(1L, 2L)
        );

        assertEquals(2, resultado.respuestasCorrectas());
        assertEquals(3, resultado.totalPreguntas());
        assertEquals(50.0, resultado.porcentaje()); // 3 de 6 puntos
        assertEquals(NivelCurso.INTERMEDIO, resultado.nivelRecomendado());
    } // Acertar 2 de 3 no es 66%: la pregunta que falló valía 3 puntos.

    @Test
    void sinResolverLasAvanzadasNoSeRecomiendaNivelAvanzado() {
        // Acierta todo menos la avanzada: 13 de 16 puntos (81%).
        EvaluacionResponse resultado = finalizarConResultados(
                bancoDeOchoPreguntas(),
                java.util.Set.of(1L, 2L, 3L, 4L, 5L, 6L, 7L)
        );

        assertEquals(NivelCurso.INTERMEDIO, resultado.nivelRecomendado());
    } // El puntaje alto no alcanza si falló las preguntas más difíciles.

    @Test
    void resolviendoLasAvanzadasSeRecomiendaNivelAvanzado() {
        EvaluacionResponse resultado = finalizarConResultados(
                bancoDeOchoPreguntas(),
                java.util.Set.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L)
        );

        assertEquals(100.0, resultado.porcentaje());
        assertEquals(NivelCurso.AVANZADO, resultado.nivelRecomendado());
    }

    @Test
    void conPuntajeBajoSeRecomiendaNivelInicial() {
        EvaluacionResponse resultado = finalizarConResultados(
                List.of(
                        crearPreguntaConDificultad(1L, 1),
                        crearPreguntaConDificultad(2L, 2),
                        crearPreguntaConDificultad(3L, 3)),
                java.util.Set.of(1L)
        );

        assertEquals(NivelCurso.INICIAL, resultado.nivelRecomendado());
    } // 1 de 6 puntos (16%) corresponde al nivel inicial.

    @Test
    void tieneEvaluacionCompletaConsultaAlRepositorio() {
        when(evaluacionRepository
                .existsByUsuario_IdAndFinalizadaTrueAndNivelElegidoNotNull(7L))
                .thenReturn(true);

        assertEquals(true, evaluacionService.tieneEvaluacionCompleta(7L));
        assertEquals(false, evaluacionService.tieneEvaluacionCompleta(8L));
    } // Una evaluación completa es una finalizada con nivel elegido.
}