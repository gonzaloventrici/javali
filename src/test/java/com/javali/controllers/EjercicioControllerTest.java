package com.javali.controllers;

import com.javali.dtos.RespuestaDTO;
import com.javali.dtos.ResultadoCorreccionDTO;
import com.javali.models.CategoriaError;
import com.javali.services.EjercicioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EjercicioControllerTest {

    @Mock
    private EjercicioService ejercicioService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void preparar() {
        EjercicioController ejercicioController =
                new EjercicioController(ejercicioService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(ejercicioController)
                .build();

        objectMapper = new ObjectMapper();
    } // Prepara el controller con un servicio simulado.

    @Test
    void responderCorrectamenteDevuelve200() throws Exception {

        Long ejercicioId = 1L;

        RespuestaDTO request = new RespuestaDTO(
                1L,
                1
        );
        ResultadoCorreccionDTO response =
                new ResultadoCorreccionDTO(
                        true,
                        null,
                        "La declaración es correcta.",
                        null
                );

        when(ejercicioService.corregir(ejercicioId, request))
                .thenReturn(response);
        mockMvc.perform(
                post("/api/ejercicios/1/respuestas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(request)
                        )
        )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.correcta").value(true)
                )
                .andExpect(
                        jsonPath("$.categoriaError").doesNotExist()
                )
                .andExpect(
                        jsonPath("$.explicacion")
                                .value("La declaración es correcta.")
                )
                .andExpect(
                        jsonPath("$.pista").doesNotExist()
                );
    } // Comprueba el JSON devuelto para una respuesta correcta.

    @Test
    void responderConErrorDeSintaxisDevuelve200()
            throws Exception {

        Long ejercicioId = 1L;

        RespuestaDTO request = new RespuestaDTO(
                1L,
                0
        );
        ResultadoCorreccionDTO response =
                new ResultadoCorreccionDTO(
                        false,
                        CategoriaError.SINTAXIS,
                        "El tipo debe escribirse antes del nombre.",
                        "Revisá el orden de int y edad."
                );

        when(ejercicioService.corregir(ejercicioId, request))
                .thenReturn(response);
        mockMvc.perform(
                post("/api/ejercicios/1/respuestas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(request)
                        )
        )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.correcta").value(false)
                )
                .andExpect(
                        jsonPath("$.categoriaError")
                                .value("SINTAXIS")
                )
                .andExpect(
                        jsonPath("$.explicacion")
                                .value(
                                        "El tipo debe escribirse "
                                                + "antes del nombre."
                                )
                )
                .andExpect(
                        jsonPath("$.pista")
                                .value(
                                        "Revisá el orden de int y edad."
                                )
                );
    } // Comprueba la categoría, explicación y pista del error.

    @Test
    void responderSinUsuarioDevuelve400()
            throws Exception {
        String jsonInvalido = """
                {
                "indiceSeleccionado": 1
                }
                """;
        mockMvc.perform(
                post("/api/ejercicios/1/respuestas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido)
        )
                .andExpect(status().isBadRequest());
    }
    // Comprueba que toda respuesta tenga un usuario asociado.
}