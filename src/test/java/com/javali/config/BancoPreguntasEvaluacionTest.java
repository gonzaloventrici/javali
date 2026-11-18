package com.javali.config;

import com.javali.dtos.PreguntaRequest;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BancoPreguntasEvaluacionTest {

    private final List<PreguntaRequest> preguntas =
            BancoPreguntasEvaluacion.preguntas();

    @Test
    void tieneDocePreguntasConOrdenConsecutivo() {
        assertEquals(12, preguntas.size());
        for (int i = 0; i < preguntas.size(); i++) {
            assertEquals(i + 1, preguntas.get(i).orden());
        }
    }

    @Test
    void cadaPreguntaTieneUnaRespuestaCorrectaDentroDeLasOpciones() {
        for (PreguntaRequest pregunta : preguntas) {
            assertTrue(pregunta.opciones().size() >= 2);
            assertTrue(pregunta.indiceCorrecto() >= 0);
            assertTrue(pregunta.indiceCorrecto() < pregunta.opciones().size());
        }
    }

    @Test
    void incluyeLasTresDificultadesYVaDeLoBasicoALoAvanzado() {
        Set<Integer> dificultades = new HashSet<>();
        int anterior = 1;
        for (PreguntaRequest pregunta : preguntas) {
            dificultades.add(pregunta.dificultad());
            assertTrue(pregunta.dificultad() >= anterior);
            anterior = pregunta.dificultad();
        }
        assertEquals(Set.of(1, 2, 3), dificultades);
    }

    @Test
    void laRespuestaCorrectaNoEstaSiempreEnLaMismaPosicion() {
        Set<Integer> posiciones = new HashSet<>();
        for (PreguntaRequest pregunta : preguntas) {
            posiciones.add(pregunta.indiceCorrecto());
        }
        assertTrue(posiciones.size() >= 3);
    }

    @Test
    void ningunaOpcionVieneVacia() {
        for (PreguntaRequest pregunta : preguntas) {
            for (String opcion : pregunta.opciones()) {
                assertFalse(opcion.isBlank());
            }
        }
    }
}
