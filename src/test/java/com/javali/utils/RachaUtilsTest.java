package com.javali.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RachaUtilsTest {

    private final LocalDate hoy = LocalDate.of(2026, 11, 18);

    @Test
    void sinDiasLaRachaEsCero() {
        assertEquals(0, RachaUtils.rachaActual(List.of(), hoy));
        assertEquals(0, RachaUtils.mejorRacha(List.of()));
    }

    @Test
    void cuentaDiasConsecutivosTerminandoHoy() {
        List<LocalDate> dias = List.of(
                hoy, hoy.minusDays(1), hoy.minusDays(2));
        assertEquals(3, RachaUtils.rachaActual(dias, hoy));
    }

    @Test
    void laRachaSigueVivaSiSePracticoAyer() {
        List<LocalDate> dias = List.of(hoy.minusDays(1), hoy.minusDays(2));
        assertEquals(2, RachaUtils.rachaActual(dias, hoy));
    }

    @Test
    void siPasoUnDiaSinPracticarLaRachaActualVuelveACero() {
        List<LocalDate> dias = List.of(hoy.minusDays(2), hoy.minusDays(3));
        assertEquals(0, RachaUtils.rachaActual(dias, hoy));
    }

    @Test
    void unHuecoCortaLaRachaPeroLaMejorSeConserva() {
        List<LocalDate> dias = List.of(
                hoy,
                hoy.minusDays(3), hoy.minusDays(4), hoy.minusDays(5));
        assertEquals(1, RachaUtils.rachaActual(dias, hoy));
        assertEquals(3, RachaUtils.mejorRacha(dias));
    }
}
