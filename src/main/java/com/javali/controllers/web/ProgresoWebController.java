package com.javali.controllers.web;

import com.javali.dtos.EvaluacionResponse;
import com.javali.exceptions.UsuarioNoEncontradoException;
import com.javali.models.NivelCurso;
import com.javali.models.Usuario;
import com.javali.repositories.UsuarioRepository;
import com.javali.services.EvaluacionService;
import com.javali.services.ProgresoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ProgresoWebController {

    private final ProgresoService progresoService;
    private final EvaluacionService evaluacionService;
    private final UsuarioRepository usuarioRepository;

    public ProgresoWebController(
            ProgresoService progresoService,
            EvaluacionService evaluacionService,
            UsuarioRepository usuarioRepository) {

        this.progresoService = progresoService;
        this.evaluacionService = evaluacionService;
        this.usuarioRepository = usuarioRepository;
    }

    //Muestra el progreso y la última evaluación finalizada del usuario autenticado.
    @GetMapping("/web/mi-progreso")
        public String mostrarProgreso(
                Authentication authentication,
                Model model) {
        Usuario usuario = buscarUsuarioAutenticado(
                authentication
        );
        List<EvaluacionResponse> evaluaciones =
                evaluacionService.listarPorUsuario(
                        usuario.getId()
                );
        EvaluacionResponse ultimaEvaluacion = null;
        for (EvaluacionResponse evaluacion : evaluaciones) {
                if (evaluacion == null
                        || !evaluacion.finalizada()) {
                continue;
                }
                ultimaEvaluacion = evaluacion;
                break;
        }
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
        model.addAttribute(
                "evaluacion",
                ultimaEvaluacion
        );
        model.addAttribute(
                "niveles",
                NivelCurso.values()
        );
        return "mi-progreso";
        }

    //Busca el usuario correspondiente a la sesión actual.
    private Usuario buscarUsuarioAutenticado(
            Authentication authentication) {
        String email = authentication
                .getName()
                .trim()
                .toLowerCase();
        return usuarioRepository
                .findByEmail(email)
                .orElseThrow(
                        () ->
                                new UsuarioNoEncontradoException(0L)
                );
    }
}