package com.javali.config;

import com.javali.models.Usuario;
import com.javali.repositories.UsuarioRepository;
import com.javali.services.EvaluacionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class EvaluacionInicialInterceptor
        implements HandlerInterceptor {

    private final UsuarioRepository usuarioRepository;
    private final EvaluacionService evaluacionService;

    public EvaluacionInicialInterceptor(
            UsuarioRepository usuarioRepository,
            EvaluacionService evaluacionService) {
        this.usuarioRepository = usuarioRepository;
        this.evaluacionService = evaluacionService;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {
        Authentication authentication =
                (Authentication) request
                        .getUserPrincipal();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication
                instanceof AnonymousAuthenticationToken) {
            return true;
        }
        boolean administrador =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals(
                                                "ROLE_ADMINISTRADOR"
                                        )
                        );
        if (administrador) {
            return true;
        }
        String email = authentication
                .getName()
                .trim()
                .toLowerCase();
        Usuario usuario = usuarioRepository
                .findByEmail(email)
                .orElse(null);
        if (usuario == null) {
            return true;
        }
        boolean evaluacionCompleta =
                evaluacionService.tieneEvaluacionCompleta(
                        usuario.getId()
                );

        if (evaluacionCompleta) {
            return true;
        }
        response.sendRedirect(
                request.getContextPath()
                        + "/web/evaluacion"
        );
        return false;
    }
}