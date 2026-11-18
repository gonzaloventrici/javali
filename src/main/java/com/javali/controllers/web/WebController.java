package com.javali.controllers.web;

import com.javali.dtos.LeccionResponse;
import com.javali.models.Usuario;
import com.javali.repositories.UsuarioRepository;
import com.javali.services.AvanceService;
import com.javali.services.CursoService;
import com.javali.services.EjercicioService;
import com.javali.services.LeccionService;
import com.javali.services.UnidadService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class WebController {

    private final CursoService cursoService;
    private final UnidadService unidadService;
    private final LeccionService leccionService;
    private final EjercicioService ejercicioService;
    private final AvanceService avanceService;
    private final UsuarioRepository usuarioRepository;

    public WebController(
        CursoService cursoService,
        UnidadService unidadService,
        LeccionService leccionService,
        EjercicioService ejercicioService,
        AvanceService avanceService,
        UsuarioRepository usuarioRepository) {
        this.cursoService = cursoService;
        this.unidadService = unidadService;
        this.leccionService = leccionService;
        this.ejercicioService = ejercicioService;
        this.avanceService = avanceService;
        this.usuarioRepository = usuarioRepository;
    }

    //Muestra la página principal.
    @GetMapping("/")
    public String inicio(
            Model model,
            Authentication authentication) {
        model.addAttribute("autenticado",authentication != null && authentication.isAuthenticated());
        if (authentication != null) {
            model.addAttribute("emailUsuario",authentication.getName());
        }
        return "inicio";
    }

    //Muestra los cursos cargados en Javali, con el avance de quien inició sesión.
    @GetMapping("/web/cursos")
    public String listarCursos(
            Model model,
            Authentication authentication) {
        Usuario usuario = buscarUsuario(authentication);
        model.addAttribute("cursos", cursoService.listar());
        model.addAttribute(
                "avance",
                usuario == null
                        ? Map.of()
                        : avanceService.estadoCursos(usuario.getId())
        );
        return "cursos";
    }

    //Muestra las unidades de un curso, incluso las que todavía están bloqueadas.
    @GetMapping("/web/cursos/{cursoId}/unidades")
    public String listarUnidades(
            @PathVariable("cursoId") Long cursoId,
            Model model,
            Authentication authentication) {
        Usuario usuario = buscarUsuario(authentication);
        model.addAttribute("curso", cursoService.obtener(cursoId));
        model.addAttribute("unidades", unidadService.listarPorCurso(cursoId));
        model.addAttribute(
                "avanceUnidades",
                usuario == null
                        ? Map.of()
                        : avanceService.estadoUnidades(usuario.getId(), cursoId)
        );
        model.addAttribute(
                "avanceCurso",
                usuario == null
                        ? null
                        : avanceService.estadoCurso(usuario.getId(), cursoId)
        );
        return "unidades";
    }

    //Muestra las lecciones pertenecientes a una unidad
    @GetMapping("/web/unidades/{unidadId}/lecciones")
    public String listarLecciones(
            @PathVariable("unidadId") Long unidadId,
            Model model,
            Authentication authentication) {
        Usuario usuario = buscarUsuario(authentication);
        model.addAttribute("unidad", unidadService.obtener(unidadId));
        model.addAttribute("lecciones", leccionService.listarPorUnidad(unidadId));
        model.addAttribute(
                "avanceLecciones",
                usuario == null
                        ? Map.of()
                        : avanceService.estadoLecciones(usuario.getId(), unidadId)
        );
        return "lecciones";
    }

    //Muestra los ejercicios dentro de una lección. Si está bloqueada,
    //vuelve a la lista de lecciones con un aviso.
    @GetMapping("/web/lecciones/{leccionId}/ejercicios")
    public String listarEjercicios(
            @PathVariable("leccionId") Long leccionId,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        LeccionResponse leccion = leccionService.obtener(leccionId);
        Usuario usuario = buscarUsuario(authentication);
        if (usuario != null
                && !avanceService.leccionDesbloqueada(usuario.getId(), leccionId)) {
            redirectAttributes.addFlashAttribute(
                    "avisoBloqueo",
                    "Esa lección todavía está bloqueada. "
                            + "Completá la anterior para abrirla."
            );
            return "redirect:/web/unidades/" + leccion.unidadId() + "/lecciones";
        }
        model.addAttribute("leccion", leccion);
        model.addAttribute(
                "ejercicios",
                ejercicioService.listarPorLeccion(leccionId)
        );
        return "ejercicios";
    }

    //Devuelve el usuario de la sesión, o null si no inició sesión.
    private Usuario buscarUsuario(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return usuarioRepository
                .findByEmail(authentication.getName().trim().toLowerCase())
                .orElse(null);
    }
}
