package com.javali.services;

import com.javali.dtos.IntentoResponse;

import java.util.List;

public interface IntentoService {

    List<IntentoResponse> listarPorUsuario(Long usuarioId);
}
// Define la consulta del historial de intentos de un usuario.