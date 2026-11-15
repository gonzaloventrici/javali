package com.javali.services;

import com.javali.dtos.RetroalimentacionRequest;
import com.javali.dtos.RetroalimentacionResponse;

public interface RetroalimentacionService {
    RetroalimentacionResponse crear(
            Long ejercicioId,
            RetroalimentacionRequest request
    );
}
// Define la operación para crear una retroalimentación.