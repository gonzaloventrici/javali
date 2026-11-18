package com.javali.validators;

import com.javali.models.TipoEjercicio;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EjercicioValidatorTest {

    private final EjercicioValidator validator = new EjercicioValidator();

    private final List<String> lineas = List.of("a", "b", "c");

    @Test
    void opcionesValidasSonAceptadas() {
        assertDoesNotThrow(() -> validator.validarEjercicio(
                TipoEjercicio.OPCIONES, lineas, 1, null));
        assertDoesNotThrow(() -> validator.validarEjercicio(
                TipoEjercicio.ENCONTRAR_ERROR, lineas, 2, null));
    }

    @Test
    void conUnaSolaOpcionLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.validarEjercicio(
                        TipoEjercicio.OPCIONES, List.of("a"), 0, null));
    }

    @Test
    void conOpcionVaciaLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.validarEjercicio(
                        TipoEjercicio.OPCIONES, List.of("a", " "), 0, null));
    }

    @Test
    void indiceCorrectoFueraDeRangoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.validarEjercicio(
                        TipoEjercicio.OPCIONES, lineas, 3, null));
        assertThrows(IllegalArgumentException.class,
                () -> validator.validarEjercicio(
                        TipoEjercicio.ENCONTRAR_ERROR, lineas, null, null));
    }

    @Test
    void ordenarExigeUnaPermutacionComoOrdenCorrecto() {
        assertDoesNotThrow(() -> validator.validarEjercicio(
                TipoEjercicio.ORDENAR, lineas, null, List.of(2, 0, 1)));
        assertThrows(IllegalArgumentException.class,
                () -> validator.validarEjercicio(
                        TipoEjercicio.ORDENAR, lineas, null, List.of(0, 1)));
        assertThrows(IllegalArgumentException.class,
                () -> validator.validarEjercicio(
                        TipoEjercicio.ORDENAR, lineas, 0, null));
    }

    @Test
    void respuestaDeOpcionDebeEstarEnRango() {
        assertDoesNotThrow(() -> validator.validarRespuesta(
                TipoEjercicio.OPCIONES, 3, 2, null));
        assertThrows(IllegalArgumentException.class,
                () -> validator.validarRespuesta(
                        TipoEjercicio.OPCIONES, 3, null, null));
        assertThrows(IllegalArgumentException.class,
                () -> validator.validarRespuesta(
                        TipoEjercicio.ENCONTRAR_ERROR, 3, 3, null));
    }

    @Test
    void respuestaDeOrdenarDebeIncluirTodasLasLineas() {
        assertDoesNotThrow(() -> validator.validarRespuesta(
                TipoEjercicio.ORDENAR, 3, null, List.of(1, 2, 0)));
        assertThrows(IllegalArgumentException.class,
                () -> validator.validarRespuesta(
                        TipoEjercicio.ORDENAR, 3, 1, null));
        assertThrows(IllegalArgumentException.class,
                () -> validator.validarRespuesta(
                        TipoEjercicio.ORDENAR, 3, null, List.of(1, 1, 0)));
    }
}
