package com.javali.controllers.web;

import com.javali.dtos.PreferenciasResponse;
import com.javali.models.Usuario;
import com.javali.repositories.UsuarioRepository;
import com.javali.models.RolUsuario;
import com.javali.services.EvaluacionService;
import com.javali.services.PreferenciasService;
import com.javali.services.RachaService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

// Datos comunes de todas las vistas web: sesión, accesibilidad y racha.
@ControllerAdvice(basePackages = "com.javali.controllers.web")
public class AutenticacionWebAdvice {

    private final UsuarioRepository usuarioRepository;
    private final PreferenciasService preferenciasService;
    private final RachaService rachaService;
    private final EvaluacionService evaluacionService;

    public AutenticacionWebAdvice(
            UsuarioRepository usuarioRepository,
            PreferenciasService preferenciasService,
            RachaService rachaService,
            EvaluacionService evaluacionService) {
        this.usuarioRepository = usuarioRepository;
        this.preferenciasService = preferenciasService;
        this.rachaService = rachaService;
        this.evaluacionService = evaluacionService;
    }

    //Informa a todas las vistas si existe un usuario autenticado,
    //aplica sus ajustes de accesibilidad y calcula la racha si la activó.
    @ModelAttribute
    public void agregarDatosComunes(
            Authentication authentication,
            Model model) {
        boolean autenticado = estaAutenticado(authentication);
        model.addAttribute("autenticado", autenticado);
        model.addAttribute(
                "emailUsuario",
                autenticado ? authentication.getName() : null
        );

        PreferenciasResponse preferencias =
                PreferenciasResponse.porDefecto();
        Usuario usuario = autenticado
                ? usuarioRepository
                        .findByEmail(
                                authentication.getName()
                                        .trim()
                                        .toLowerCase()
                        )
                        .orElse(null)
                : null;
        if (usuario != null) {
            preferencias = preferenciasService.obtener(usuario.getId());
        }
        model.addAttribute("preferencias", preferencias);

        // El enlace a la evaluación inicial solo se muestra a los estudiantes
        // que todavía no la completaron.
        model.addAttribute(
                "evaluacionPendiente",
                usuario != null
                        && usuario.getRol() == RolUsuario.ESTUDIANTE
                        && !evaluacionService.tieneEvaluacionCompleta(
                                usuario.getId()
                        )
        );
        model.addAttribute(
                "racha",
                usuario != null && preferencias.mostrarRacha()
                        ? rachaService.obtener(usuario.getId())
                        : null
        );
    }

    private boolean estaAutenticado(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication
                        instanceof AnonymousAuthenticationToken);
    }
}
