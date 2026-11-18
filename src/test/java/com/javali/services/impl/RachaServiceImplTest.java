package com.javali.services.impl;

import com.javali.dtos.RachaResponse;
import com.javali.repositories.IntentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RachaServiceImplTest {

    @Mock
    private IntentoRepository intentoRepository;

    private final LocalDate hoy = LocalDate.of(2026, 11, 18);

    @Test
    void calculaRachaActualYMejorRacha() {
        when(intentoRepository.listarDiasConIntentos(1L))
                .thenReturn(List.of(
                        hoy.minusDays(6), hoy.minusDays(5), hoy.minusDays(4),
                        hoy.minusDays(1), hoy));

        RachaResponse racha =
                new RachaServiceImpl(intentoRepository).obtener(1L, hoy);

        assertEquals(2, racha.rachaActual());
        assertEquals(3, racha.mejorRacha());
        assertTrue(racha.practicoHoy());
    }

    @Test
    void sinIntentosTodoEsCero() {
        when(intentoRepository.listarDiasConIntentos(1L))
                .thenReturn(List.of());

        RachaResponse racha =
                new RachaServiceImpl(intentoRepository).obtener(1L, hoy);

        assertEquals(0, racha.rachaActual());
        assertEquals(0, racha.mejorRacha());
        assertFalse(racha.practicoHoy());
    }
}
