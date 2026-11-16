package com.javali.controllers.web;

import com.javali.exceptions.UsuarioNoEncontradoException;
import com.javali.models.Usuario;
import com.javali.repositories.UsuarioRepository;
import com.javali.services.ProgresoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProgresoWebController {

    private final ProgresoService progresoService;
    private final UsuarioRepository usuarioRepository;

    public ProgresoWebController(
            ProgresoService progresoService,
            UsuarioRepository usuarioRepository) {

        this.progresoService = progresoService;
        this.usuarioRepository = usuarioRepository;
    }

    // Muestra el progreso del usuario autenticado.
    @GetMapping("/web/mi-progreso")
    public String mostrarProgreso(
            Authentication authentication,
            Model model) {
        String email = authentication
                .getName()
                .trim()
                .toLowerCase();
        Usuario usuario = usuarioRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new UsuarioNoEncontradoException(0L)
                );
        model.addAttribute(
                "usuario",
                usuario
        );
        model.addAttribute(
                "resumen",
                progresoService.obtenerResumen(
                        usuario.getId()
                )
        );
        model.addAttribute(
                "progresos",
                progresoService.listarPorUsuario(
                        usuario.getId()
                )
        );
        return "mi-progreso";
    }
}