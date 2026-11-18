package com.javali.utils;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrdenUtilsTest {

    @Test
    void permutacionValidaEsAceptada() {
        assertTrue(OrdenUtils.esPermutacion(List.of(2, 0, 1), 3));
    }

    @Test
    void listaConRepetidosOFaltantesNoEsPermutacion() {
        assertFalse(OrdenUtils.esPermutacion(List.of(0, 0, 1), 3));
        assertFalse(OrdenUtils.esPermutacion(List.of(0, 1), 3));
        assertFalse(OrdenUtils.esPermutacion(List.of(0, 1, 3), 3));
        assertFalse(OrdenUtils.esPermutacion(List.of(-1, 0, 1), 3));
    }

    @Test
    void listaNulaOConNulosNoEsPermutacion() {
        assertFalse(OrdenUtils.esPermutacion(null, 3));
        assertFalse(OrdenUtils.esPermutacion(
                Arrays.asList(0, null, 2), 3));
    }

    @Test
    void aTextoUneLosIndicesConComas() {
        assertEquals("2,0,1", OrdenUtils.aTexto(List.of(2, 0, 1)));
        assertNull(OrdenUtils.aTexto(null));
    }
}
