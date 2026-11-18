package com.javali.utils;

import java.time.LocalDate;
import java.util.Collection;
import java.util.TreeSet;

// Cálculo de días seguidos de práctica.
public final class RachaUtils {

    private RachaUtils() {}

    // La racha sigue viva si se practicó hoy o ayer.
    public static int rachaActual(Collection<LocalDate> dias, LocalDate hoy) {
        TreeSet<LocalDate> ordenados = new TreeSet<>(dias);
        LocalDate dia;
        if (ordenados.contains(hoy)) {
            dia = hoy;
        } else if (ordenados.contains(hoy.minusDays(1))) {
            dia = hoy.minusDays(1);
        } else {
            return 0;
        }
        int racha = 0;
        while (ordenados.contains(dia)) {
            racha++;
            dia = dia.minusDays(1);
        }
        return racha;
    }

    // Devuelve la mayor cantidad de días consecutivos registrados.
    public static int mejorRacha(Collection<LocalDate> dias) {
        TreeSet<LocalDate> ordenados = new TreeSet<>(dias);
        int mejor = 0;
        int actual = 0;
        LocalDate anterior = null;
        for (LocalDate dia : ordenados) {
            if (anterior != null && dia.equals(anterior.plusDays(1))) {
                actual++;
            } else {
                actual = 1;
            }
            mejor = Math.max(mejor, actual);
            anterior = dia;
        }
        return mejor;
    }
}
