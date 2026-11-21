package com.javali.services;

import com.javali.dtos.LoginRequest;
import com.javali.dtos.LoginResponse;

public interface AuthService {

    LoginResponse iniciarSesion(LoginRequest request);
}
// Define el inicio de sesión que entrega un token JWT.