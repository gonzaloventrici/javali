package com.javali.controllers.web;

import com.javali.dtos.RespuestaDTO;
import com.javali.dtos.ResultadoCorreccionDTO;
import com.javali.exceptions.UsuarioNoEncontradoException;
import com.javali.models.Usuario;
import com.javali.repositories.UsuarioRepository;
import com.javali.services.EjercicioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ResolverEjercicioWebController {

    private final EjercicioService ejercicioService;
    private final UsuarioRepository usuarioRepository;

    public ResolverEjercicioWebController(
            EjercicioService ejercicioService,
            UsuarioRepository usuarioRepository) {

        this.ejercicioService = ejercicioService;
        this.usuarioRepository = usuarioRepository;
    }

    //Muestra un ejercicio para que el usuario autenticado
    //seleccione una respuesta.
    @GetMapping("/web/ejercicios/{ejercicioId}")
    public String mostrarEjercicio(
            @PathVariable("ejercicioId") Long ejercicioId,
            Model model) {
        model.addAttribute(
                "ejercicio",
                ejercicioService.obtener(ejercicioId)
        );
        return "resolver-ejercicio";
    }

    //Corrige la opción elegida y registra el intento
    //para el usuario autenticado.
    @PostMapping("/web/ejercicios/{ejercicioId}/respuesta")
    public String responder(
            @PathVariable("ejercicioId") Long ejercicioId,
            @RequestParam("indiceSeleccionado")
            Integer indiceSeleccionado,
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
        RespuestaDTO respuesta = new RespuestaDTO(
                usuario.getId(),
                indiceSeleccionado
        );
        ResultadoCorreccionDTO resultado =
                ejercicioService.corregir(
                        ejercicioId,
                        respuesta
                );
        model.addAttribute(
                "ejercicio",
                ejercicioService.obtener(ejercicioId)
        );
        model.addAttribute(
                "resultado",
                resultado
        );
        model.addAttribute(
                "indiceSeleccionado",
                indiceSeleccionado
        );
        return "resolver-ejercicio";
    }
}