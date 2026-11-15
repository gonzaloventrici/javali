package com.javali.services;

import com.javali.dtos.UsuarioRequest;
import com.javali.dtos.UsuarioResponse;

public interface UsuarioService {

    UsuarioResponse registrar(UsuarioRequest request);

    UsuarioResponse obtener(Long id);
}
// Define las operaciones para registrar y consultar usuarios.