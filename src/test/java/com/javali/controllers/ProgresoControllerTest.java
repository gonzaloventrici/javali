package com.javali.controllers;

import com.javali.dtos.ProgresoResponse;
import com.javali.dtos.ResumenProgresoResponse;
import com.javali.models.CategoriaError;
import com.javali.services.ProgresoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.javali.dtos.EstadoLeccionResponse;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProgresoControllerTest {

    @Mock
    private ProgresoService progresoService;

    private MockMvc mockMvc;

    @BeforeEach
    void preparar() {
        ProgresoController progresoController =
                new ProgresoController(progresoService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(progresoController)
                .build();
    } // Prepara el controller con un servicio simulado.

    @Test
    void listarProgresosDevuelve200() throws Exception {
        Long usuarioId = 1L;
        ProgresoResponse progreso = new ProgresoResponse(
                1L,
                usuarioId,
                1L,
                "Declaración de variables",
                5,
                4,
                80.0,
                true,
                LocalDateTime.of(
                        2026,
                        11,
                        15,
                        15,
                        30
                )
        );

        when(progresoService.listarPorUsuario(usuarioId))
                .thenReturn(List.of(progreso));
        mockMvc.perform(
                get("/api/usuarios/1/progresos")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].usuarioId").value(1))
                .andExpect(jsonPath("$[0].leccionId").value(1))
                .andExpect(
                        jsonPath("$[0].tituloLeccion")
                                .value("Declaración de variables")
                )
                .andExpect(
                        jsonPath("$[0].cantidadIntentos")
                                .value(5)
                )
                .andExpect(
                        jsonPath("$[0].respuestasCorrectas")
                                .value(4)
                )
                .andExpect(
                        jsonPath("$[0].porcentajeAciertos")
                                .value(80.0)
                )
                .andExpect(
                        jsonPath("$[0].completada")
                                .value(true)
                );
    } // Comprueba el listado de progresos por lección.

    @Test
    void obtenerResumenDevuelve200() throws Exception {

        Long usuarioId = 1L;

        ResumenProgresoResponse resumen =
                new ResumenProgresoResponse(
                        usuarioId,
                        6,
                        4,
                        66.66666666666667,
                        CategoriaError.SINTAXIS,
                        List.of("Variables"),
                        List.of("Condicionales"),
                        "Practicar la escritura y estructura de código Java."
                );

        when(progresoService.obtenerResumen(usuarioId))
                .thenReturn(resumen);
        mockMvc.perform(
                get("/api/usuarios/1/progreso/resumen")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioId").value(1))
                .andExpect(jsonPath("$.totalIntentos").value(6))
                .andExpect(
                        jsonPath("$.respuestasCorrectas")
                                .value(4)
                )
                .andExpect(
                        jsonPath("$.errorFrecuente")
                                .value("SINTAXIS")
                )
                .andExpect(
                        jsonPath("$.fortalezas[0]")
                                .value("Variables")
                )
                .andExpect(
                        jsonPath("$.temasPendientes[0]")
                                .value("Condicionales")
                )
                .andExpect(
                        jsonPath("$.recomendacion")
                                .value(
                                        "Practicar la escritura y "
                                                + "estructura de código Java."
                                )
                );
    } // Comprueba fortalezas, pendientes y recomendación del resumen.

    @Test
    void obtenerEstadoLeccionDesbloqueadaDevuelve200()
        throws Exception {

        Long usuarioId = 1L;
        Long leccionId = 2L;

        EstadoLeccionResponse estado =
                new EstadoLeccionResponse(
                        leccionId,
                        "Tipos primitivos",
                        true,
                        false,
                        0.0
                );

        when(progresoService.obtenerEstadoLeccion(
                usuarioId,
                leccionId
        )).thenReturn(estado);
        mockMvc.perform(
                get("/api/usuarios/1/lecciones/2/estado")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.leccionId").value(2))
                .andExpect(
                        jsonPath("$.titulo")
                                .value("Tipos primitivos")
                )
                .andExpect(
                        jsonPath("$.desbloqueada").value(true)
                )
                .andExpect(
                        jsonPath("$.completada").value(false)
                )
                .andExpect(
                        jsonPath("$.porcentajeAciertos").value(0.0)
                );
        } // Comprueba el endpoint que informa el desbloqueo de una lección.
}