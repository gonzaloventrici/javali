package com.javali.controllers;

import com.javali.dtos.EvaluacionResponse;
import com.javali.dtos.FinalizarEvaluacionRequest;
import com.javali.dtos.NivelElegidoRequest;
import com.javali.dtos.RespuestaPreguntaDTO;
import com.javali.models.NivelCurso;
import com.javali.services.EvaluacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EvaluacionControllerTest {

    @Mock
    private EvaluacionService evaluacionService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void preparar() {

        EvaluacionController evaluacionController =
                new EvaluacionController(evaluacionService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(evaluacionController)
                .build();

        objectMapper = new ObjectMapper();
    } // Prepara el controller con un servicio simulado.

    @Test
    void iniciarEvaluacionDevuelve201() throws Exception {
        Long usuarioId = 1L;

        EvaluacionResponse response = new EvaluacionResponse(
                5L,
                usuarioId,
                0,
                0,
                0.0,
                NivelCurso.INICIAL,
                null,
                false,
                LocalDateTime.of(
                        2026,
                        11,
                        15,
                        15,
                        30
                )
        );
        when(evaluacionService.iniciar(usuarioId))
                .thenReturn(response);
        mockMvc.perform(
                post("/api/usuarios/1/evaluaciones")
        )
                .andExpect(status().isCreated())
                .andExpect(
                        header().string(
                                "Location",
                                "/api/evaluaciones/5"
                        )
                )
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.usuarioId").value(1))
                .andExpect(
                        jsonPath("$.nivelRecomendado")
                                .value("INICIAL")
                )
                .andExpect(
                        jsonPath("$.nivelElegido")
                                .doesNotExist()
                )
                .andExpect(
                        jsonPath("$.finalizada")
                                .value(false)
                );
    } // Comprueba la creación de una evaluación pendiente.

    @Test
    void finalizarEvaluacionDevuelveNivelAvanzado()
            throws Exception {
        Long evaluacionId = 5L;

        FinalizarEvaluacionRequest request =
                new FinalizarEvaluacionRequest(
                        List.of(
                                new RespuestaPreguntaDTO(1L, 1),
                                new RespuestaPreguntaDTO(2L, 2)
                        )
                );
        EvaluacionResponse response = new EvaluacionResponse(
                evaluacionId,
                1L,
                2,
                2,
                100.0,
                NivelCurso.AVANZADO,
                null,
                true,
                LocalDateTime.of(
                        2026,
                        11,
                        15,
                        15,
                        35
                )
        );
        when(evaluacionService.finalizar(
                evaluacionId,
                request
        )).thenReturn(response);
        mockMvc.perform(
                post("/api/evaluaciones/5/finalizacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(request)
                        )
        )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.respuestasCorrectas")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$.totalPreguntas")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$.porcentaje")
                                .value(100.0)
                )
                .andExpect(
                        jsonPath("$.nivelRecomendado")
                                .value("AVANZADO")
                )
                .andExpect(
                        jsonPath("$.finalizada")
                                .value(true)
                );
    } // Comprueba la finalización y el nivel recomendado.

    @Test
    void elegirNivelIntermedioDevuelve200()
            throws Exception {
        Long evaluacionId = 5L;

        NivelElegidoRequest request =
                new NivelElegidoRequest(
                        NivelCurso.INTERMEDIO
                );
        EvaluacionResponse response = new EvaluacionResponse(
                evaluacionId,
                1L,
                2,
                2,
                100.0,
                NivelCurso.AVANZADO,
                NivelCurso.INTERMEDIO,
                true,
                LocalDateTime.of(
                        2026,
                        11,
                        15,
                        15,
                        35
                )
        );
        when(evaluacionService.elegirNivel(
                evaluacionId,
                request
        )).thenReturn(response);
        mockMvc.perform(
                patch("/api/evaluaciones/5/nivel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(request)
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.nivelRecomendado")
                                .value("AVANZADO")
                )
                .andExpect(
                        jsonPath("$.nivelElegido")
                                .value("INTERMEDIO")
                )
                .andExpect(
                        jsonPath("$.finalizada")
                                .value(true)
                );
    } // Comprueba que el estudiante pueda cambiar el nivel recomendado.

    @Test
    void finalizarSinRespuestasDevuelve400()
            throws Exception {
        String jsonInvalido = """
                {
                "respuestas": []
                }
                """;
        mockMvc.perform(
                post("/api/evaluaciones/5/finalizacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido)
        )
                .andExpect(status().isBadRequest());
    } // Comprueba que no pueda finalizarse una evaluación sin respuestas.
}