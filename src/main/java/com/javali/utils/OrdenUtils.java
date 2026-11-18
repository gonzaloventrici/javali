package com.javali.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// Funciones auxiliares para los ejercicios de ordenar líneas.
public final class OrdenUtils {

    private OrdenUtils() {}

    // Indica si la lista contiene cada índice de 0 a tamano-1 exactamente una vez.
    public static boolean esPermutacion(List<Integer> orden, int tamano) {
        if (orden == null || orden.size() != tamano) {
            return false;
        }
        Set<Integer> vistos = new HashSet<>();
        for (Integer indice : orden) {
            if (indice == null || indice < 0 || indice >= tamano) {
                return false;
            }
            if (!vistos.add(indice)) {
                return false;
            }
        }
        return true;
    }

    // Convierte [2, 0, 1] en "2,0,1" para guardarlo en el intento.
    public static String aTexto(List<Integer> orden) {
        if (orden == null) {
            return null;
        }
        return orden.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }
}
