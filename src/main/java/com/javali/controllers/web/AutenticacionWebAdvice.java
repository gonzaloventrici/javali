package com.javali.controllers.web;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class AutenticacionWebAdvice {

    //Informa a todas las vistas si existe un usuario autenticado.
    @ModelAttribute("autenticado")
    public boolean autenticado(
            Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication
                        instanceof AnonymousAuthenticationToken);
    }

    //Expone el email del usuario autenticado a todas las vistas.
    @ModelAttribute("emailUsuario")
    public String emailUsuario(
            Authentication authentication) {
        if (!autenticado(authentication)) {
            return null;
        }
        return authentication.getName();
    }
}