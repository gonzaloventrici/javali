package com.javali.services;

import com.javali.dtos.RachaResponse;

import java.time.LocalDate;

public interface RachaService {

    RachaResponse obtener(Long usuarioId);
    // Calcula la racha de práctica tomando como referencia el día de hoy.

    RachaResponse obtener(Long usuarioId, LocalDate hoy);
    // Igual que el anterior pero con una fecha indicada (útil para probar).
}
