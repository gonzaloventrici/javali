package com.javali.controllers.web;

import com.javali.dtos.EvaluacionResponse;
import com.javali.dtos.FinalizarEvaluacionRequest;
import com.javali.dtos.NivelElegidoRequest;
import com.javali.dtos.PreguntaRequest;
import com.javali.dtos.PreguntaResponse;
import com.javali.dtos.RespuestaPreguntaDTO;
import com.javali.exceptions.RespuestaEvaluacionInvalidaException;
import com.javali.exceptions.UsuarioNoEncontradoException;
import com.javali.models.NivelCurso;
import com.javali.models.Usuario;
import com.javali.repositories.UsuarioRepository;
import com.javali.services.EvaluacionService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class EvaluacionWebController {

    private final EvaluacionService evaluacionService;
    private final UsuarioRepository usuarioRepository;

    public EvaluacionWebController(
            EvaluacionService evaluacionService,
            UsuarioRepository usuarioRepository) {

        this.evaluacionService = evaluacionService;
        this.usuarioRepository = usuarioRepository;
    }

    //Crea o recupera una evaluación pendiente para el usuario autenticado.
    @GetMapping("/web/evaluacion")
    public String mostrarEvaluacion(
            Authentication authentication,
            Model model) {
        Usuario usuario = buscarUsuario(authentication);
        EvaluacionResponse evaluacion =
                buscarEvaluacionPendiente(usuario.getId());
        List<PreguntaResponse> preguntas =
                evaluacionService.listarPreguntas(
                        evaluacion.id()
                );
        if (preguntas.isEmpty()) {
            cargarPreguntas(evaluacion.id());

            preguntas = evaluacionService.listarPreguntas(
                    evaluacion.id()
            );
        }
        model.addAttribute(
                "evaluacion",
                evaluacion
        );
        model.addAttribute(
                "preguntas",
                preguntas
        );
        return "evaluacion";
    }

    //Corrige todas las respuestas y calcula el nivel recomendado.
    @PostMapping(
            "/web/evaluaciones/{evaluacionId}/finalizar"
    )
    public String finalizar(
            @PathVariable("evaluacionId")
            Long evaluacionId,
            @RequestParam
            Map<String, String> parametros,
            Authentication authentication,
            Model model) {
        Usuario usuario = buscarUsuario(authentication);
        verificarPropietario(
                usuario.getId(),
                evaluacionId
        );
        List<PreguntaResponse> preguntas =
                evaluacionService.listarPreguntas(
                        evaluacionId
                );
        List<RespuestaPreguntaDTO> respuestas =
                construirRespuestas(
                        preguntas,
                        parametros
                );
        FinalizarEvaluacionRequest request =
                new FinalizarEvaluacionRequest(
                        respuestas
                );
        EvaluacionResponse resultado =
                evaluacionService.finalizar(
                        evaluacionId,
                        request
                );
        model.addAttribute(
                "evaluacion",
                resultado
        );
        model.addAttribute(
                "niveles",
                NivelCurso.values()
        );
        return "resultado-evaluacion";
    }

    //Guarda el nivel seleccionado por el estudiante.
    @PostMapping(
            "/web/evaluaciones/{evaluacionId}/nivel"
    )
    public String elegirNivel(
            @PathVariable("evaluacionId")
            Long evaluacionId,
            @RequestParam("nivelElegido")
            NivelCurso nivel,
            Authentication authentication) {
        Usuario usuario = buscarUsuario(authentication);
        EvaluacionResponse evaluacion =
                verificarPropietario(
                        usuario.getId(),
                        evaluacionId
                );
        if (!evaluacion.finalizada()) {
            throw new RespuestaEvaluacionInvalidaException(
                    "Primero debe finalizar la evaluación"
            );
        }
        evaluacionService.elegirNivel(
                evaluacionId,
                new NivelElegidoRequest(nivel)
        );
        return "redirect:/web/mi-progreso";
    }

    //Recupera una evaluación pendiente o crea una nueva.
    private EvaluacionResponse buscarEvaluacionPendiente(
            Long usuarioId) {
        return evaluacionService
                .listarPorUsuario(usuarioId)
                .stream()
                .filter(evaluacion ->
                        evaluacion != null
                                && !evaluacion.finalizada()
                )
                .findFirst()
                .orElseGet(
                        () -> evaluacionService
                                .iniciar(usuarioId)
                );
    }

    //Construye las respuestas desde los parámetros HTML.
    private List<RespuestaPreguntaDTO>
            construirRespuestas(
                    List<PreguntaResponse> preguntas,
                    Map<String, String> parametros) {
        List<RespuestaPreguntaDTO> respuestas =
                new ArrayList<>();
        for (PreguntaResponse pregunta : preguntas) {
            String nombreParametro =
                    "respuesta_" + pregunta.id();
            String valor = parametros.get(
                    nombreParametro
            );
            if (valor == null || valor.isBlank()) {
                throw new RespuestaEvaluacionInvalidaException(
                        "Debe responder todas las preguntas"
                );
            }
            respuestas.add(
                    new RespuestaPreguntaDTO(
                            pregunta.id(),
                            Integer.valueOf(valor)
                    )
            );
        }
        return respuestas;
    }

    //Comprueba que la evaluación pertenezca al usuario.
    private EvaluacionResponse verificarPropietario(
            Long usuarioId,
            Long evaluacionId) {
        return evaluacionService
                .listarPorUsuario(usuarioId)
                .stream()
                .filter(evaluacion ->
                        evaluacion != null
                                && evaluacion.id()
                                .equals(evaluacionId)
                )
                .findFirst()
                .orElseThrow(
                        () ->
                                new RespuestaEvaluacionInvalidaException(
                                        "La evaluación no pertenece "
                                                + "al usuario autenticado"
                                )
                );
    }

    //Recupera el usuario de la sesión actual.
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

    //Agrega las preguntas de diagnóstico a una evaluación.
    private void cargarPreguntas(Long evaluacionId) {
        evaluacionService.agregarPregunta(
                evaluacionId,
                new PreguntaRequest(
                        "¿Cuál declaración crea correctamente "
                                + "una variable entera?",
                        null,
                        1,
                        List.of(
                                "edad int = 20;",
                                "int edad = 20;",
                                "variable edad = 20;"
                        ),
                        1
                )
        );
        evaluacionService.agregarPregunta(
                evaluacionId,
                new PreguntaRequest(
                        "¿Qué tipo permite almacenar "
                                + "números con decimales?",
                        null,
                        2,
                        List.of(
                                "boolean",
                                "double",
                                "char",
                                "int"
                        ),
                        1
                )
        );
        evaluacionService.agregarPregunta(
                evaluacionId,
                new PreguntaRequest(
                        "¿Qué operador comprueba si "
                                + "dos valores son iguales?",
                        null,
                        3,
                        List.of(
                                "=",
                                "==",
                                "!=",
                                ">="
                        ),
                        1
                )
        );
        evaluacionService.agregarPregunta(
                evaluacionId,
                new PreguntaRequest(
                        "¿Qué muestra este código?",
                        """
                        int edad = 20;

                        if (edad >= 18) {
                            System.out.println("Mayor");
                        } else {
                            System.out.println("Menor");
                        }
                        """,
                        4,
                        List.of(
                                "Mayor",
                                "Menor",
                                "20",
                                "No compila"
                        ),
                        0
                )
        );
        evaluacionService.agregarPregunta(
                evaluacionId,
                new PreguntaRequest(
                        "¿Cuántas veces se ejecuta "
                                + "el cuerpo del ciclo?",
                        """
                        for (int i = 0; i < 3; i++) {
                            System.out.println(i);
                        }
                        """,
                        5,
                        List.of(
                                "2",
                                "3",
                                "4",
                                "Infinitas"
                        ),
                        1
                )
        );
    }
}