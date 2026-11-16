package com.javali.controllers.web;

import com.javali.services.CursoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.javali.services.UnidadService;
import org.springframework.web.bind.annotation.PathVariable;
import com.javali.services.LeccionService;
import com.javali.services.EjercicioService;

@Controller
public class WebController {

    private final CursoService cursoService;
    private final UnidadService unidadService;
    private final LeccionService leccionService;
    private final EjercicioService ejercicioService;

    public WebController(
        CursoService cursoService,
        UnidadService unidadService,
        LeccionService leccionService,
        EjercicioService ejercicioService) {
        this.cursoService = cursoService;
        this.unidadService = unidadService;
        this.leccionService = leccionService;
        this.ejercicioService = ejercicioService;
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

    //Muestra los cursos cargados en Javali.
    @GetMapping("/web/cursos")
    public String listarCursos(Model model) {
        model.addAttribute("cursos", cursoService.listar());
        return "cursos";
    }

    //Muestra las unidades pertenecientes a un curso
    @GetMapping("/web/cursos/{cursoId}/unidades")
    public String listarUnidades(
            @PathVariable("cursoId") Long cursoId,
            Model model) {
        model.addAttribute(
                "curso",
                cursoService.obtener(cursoId)
        );
        model.addAttribute(
                "unidades",
                unidadService.listarPorCurso(cursoId)
        );
        return "unidades";
    }

    //Muestra las lecciones pertenecientes a una unidad
    @GetMapping("/web/unidades/{unidadId}/lecciones")
    public String listarLecciones(
            @PathVariable("unidadId") Long unidadId,
            Model model) {
        model.addAttribute(
                "unidad",
                unidadService.obtener(unidadId)
        );
        model.addAttribute(
                "lecciones",
                leccionService.listarPorUnidad(unidadId)
        );
        return "lecciones";
    }

    //Muestra los ejercicios dentro de una lección
    @GetMapping("/web/lecciones/{leccionId}/ejercicios")
    public String listarEjercicios(
            @PathVariable("leccionId") Long leccionId,
            Model model) {
        model.addAttribute(
                "leccion",
                leccionService.obtener(leccionId)
        );
        model.addAttribute(
                "ejercicios",
                ejercicioService.listarPorLeccion(leccionId)
        );
        return "ejercicios";
    }
}