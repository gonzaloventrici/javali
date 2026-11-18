package com.javali.controllers.web;

import com.javali.dtos.EjercicioResponse;
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

import java.util.List;

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

    // Muestra un ejercicio para que el usuario autenticado
    // seleccione una respuesta.
    @GetMapping("/web/ejercicios/{ejercicioId}")
    public String mostrarEjercicio(
            @PathVariable("ejercicioId") Long ejercicioId,
            Model model) {
        EjercicioResponse ejercicio =
                ejercicioService.obtener(ejercicioId);
        model.addAttribute(
                "ejercicio",
                ejercicio
        );
        return "resolver-ejercicio";
    }

    //Corrige la respuesta, registra el intento y obtiene
    //el siguiente ejercicio de la misma lección.
    @PostMapping(
            "/web/ejercicios/{ejercicioId}/respuesta"
    )
    public String responder(
            @PathVariable("ejercicioId") Long ejercicioId,
            @RequestParam("indiceSeleccionado")
            Integer indiceSeleccionado,
            Authentication authentication,
            Model model) {
        Usuario usuario = buscarUsuario(
                authentication
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
        EjercicioResponse ejercicioActual =
                ejercicioService.obtener(
                        ejercicioId
                );
        Long siguienteEjercicioId =
                buscarSiguienteEjercicio(
                        ejercicioActual
                );
        model.addAttribute(
                "ejercicio",
                ejercicioActual
        );
        model.addAttribute(
                "resultado",
                resultado
        );
        model.addAttribute(
                "indiceSeleccionado",
                indiceSeleccionado
        );
        model.addAttribute(
                "siguienteEjercicioId",
                siguienteEjercicioId
        );
        return "resolver-ejercicio";
    }

    //Obtiene el usuario correspondiente a la sesión actual.
    private Usuario buscarUsuario(
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

    //Obtiene el ejercicio siguiente dentro de la lección.
    private Long buscarSiguienteEjercicio(
            EjercicioResponse ejercicioActual) {

        List<EjercicioResponse> ejercicios =
                ejercicioService.listarPorLeccion(
                        ejercicioActual.leccionId()
                );
        for (int indice = 0;
                indice < ejercicios.size();
                indice++) {
            EjercicioResponse ejercicio =
                    ejercicios.get(indice);
            if (!ejercicio.id()
                    .equals(ejercicioActual.id())) {

                continue;
            }
            int siguienteIndice = indice + 1;
            if (siguienteIndice
                    < ejercicios.size()) {

                return ejercicios
                        .get(siguienteIndice)
                        .id();
            }
            return null;
        }
        return null;
    }
}