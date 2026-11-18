package com.javali.controllers.web;

import com.javali.dtos.EjercicioResponse;
import com.javali.dtos.LineaEjercicioDTO;
import com.javali.dtos.RespuestaDTO;
import com.javali.dtos.ResultadoCorreccionDTO;
import com.javali.exceptions.UsuarioNoEncontradoException;
import com.javali.models.Usuario;
import com.javali.repositories.UsuarioRepository;
import com.javali.services.AvanceService;
import com.javali.services.EjercicioService;
import com.javali.utils.OrdenUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ResolverEjercicioWebController {

    private final EjercicioService ejercicioService;
    private final UsuarioRepository usuarioRepository;
    private final AvanceService avanceService;

    public ResolverEjercicioWebController(
            EjercicioService ejercicioService,
            UsuarioRepository usuarioRepository,
            AvanceService avanceService) {

        this.ejercicioService = ejercicioService;
        this.usuarioRepository = usuarioRepository;
        this.avanceService = avanceService;
        }

    // Muestra un ejercicio para que el usuario autenticado
    // lo resuelva según su tipo.
    @GetMapping("/web/ejercicios/{ejercicioId}")
    public String mostrarEjercicio(
            @PathVariable("ejercicioId") Long ejercicioId,
            Authentication authentication,
            Model model) {
        EjercicioResponse ejercicio =
                ejercicioService.obtener(ejercicioId);
        Usuario usuarioActual = buscarUsuario(authentication);
        if (leccionBloqueada(usuarioActual, ejercicio)) {
            return redirigirALeccion(ejercicio);
        }
        model.addAttribute("ejercicio", ejercicio);
        model.addAttribute(
                "lineas",
                crearLineas(ejercicio, null)
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
            @RequestParam(
                    name = "indiceSeleccionado",
                    required = false) Integer indiceSeleccionado,
            @RequestParam(
                    name = "ordenSeleccionado",
                    required = false) List<Integer> ordenSeleccionado,
            Authentication authentication,
            Model model) {
        Usuario usuario = buscarUsuario(
                authentication
        );
        EjercicioResponse ejercicioActual =
                ejercicioService.obtener(
                        ejercicioId
                );
        if (leccionBloqueada(usuario, ejercicioActual)) {
            return redirigirALeccion(ejercicioActual);
        }
        model.addAttribute("ejercicio", ejercicioActual);
        model.addAttribute(
                "lineas",
                crearLineas(ejercicioActual, ordenSeleccionado)
        );
        model.addAttribute(
                "indiceSeleccionado",
                indiceSeleccionado
        );
        RespuestaDTO respuesta = new RespuestaDTO(
                usuario.getId(),
                indiceSeleccionado,
                ordenSeleccionado
        );
        ResultadoCorreccionDTO resultado;
        try {
            resultado = ejercicioService.corregir(
                    ejercicioId,
                    respuesta
            );
        } catch (IllegalArgumentException exception) {
            // Respuesta incompleta: se vuelve a mostrar el ejercicio con un aviso.
            model.addAttribute(
                    "errorRespuesta",
                    exception.getMessage()
            );
            return "resolver-ejercicio";
        }
        model.addAttribute("resultado", resultado);
        model.addAttribute(
                "siguienteEjercicioId",
                buscarSiguienteEjercicio(ejercicioActual)
        );
        return "resolver-ejercicio";
    }

    //Indica si la lección del ejercicio todavía no está habilitada para el usuario.
    private boolean leccionBloqueada(
            Usuario usuario,
            EjercicioResponse ejercicio) {
        return !avanceService.leccionDesbloqueada(
                usuario.getId(),
                ejercicio.leccionId()
        );
    }

    //Vuelve a la lista de ejercicios de la lección, que explica el bloqueo.
    private String redirigirALeccion(EjercicioResponse ejercicio) {
        return "redirect:/web/lecciones/" + ejercicio.leccionId() + "/ejercicios";
    }

    //Arma las líneas de un ejercicio de ordenar: en el orden guardado
    //la primera vez y en el orden elegido por el estudiante después.
    private List<LineaEjercicioDTO> crearLineas(
            EjercicioResponse ejercicio,
            List<Integer> orden) {
        List<String> opciones = ejercicio.opciones();
        List<LineaEjercicioDTO> lineas = new ArrayList<>();
        if (OrdenUtils.esPermutacion(orden, opciones.size())) {
            for (Integer indice : orden) {
                lineas.add(
                        new LineaEjercicioDTO(
                                indice,
                                opciones.get(indice)
                        )
                );
            }
            return lineas;
        }
        for (int indice = 0; indice < opciones.size(); indice++) {
            lineas.add(
                    new LineaEjercicioDTO(
                            indice,
                            opciones.get(indice)
                    )
            );
        }
        return lineas;
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