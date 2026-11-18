package com.javali.controllers.web;

import com.javali.dtos.LeccionResponse;
import com.javali.models.Usuario;
import com.javali.repositories.UsuarioRepository;
import com.javali.services.AvanceService;
import com.javali.services.CursoService;
import com.javali.services.EjercicioService;
import com.javali.services.LeccionService;
import com.javali.services.UnidadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class WebControllerTest {

    @Mock private CursoService cursoService;
    @Mock private UnidadService unidadService;
    @Mock private LeccionService leccionService;
    @Mock private EjercicioService ejercicioService;
    @Mock private AvanceService avanceService;
    @Mock private UsuarioRepository usuarioRepository;

    private MockMvc mockMvc;

    private final UsernamePasswordAuthenticationToken autenticacion =
            new UsernamePasswordAuthenticationToken(
                    "estudiante@javali.com", "x");

    @BeforeEach
    void preparar() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new WebController(
                        cursoService,
                        unidadService,
                        leccionService,
                        ejercicioService,
                        avanceService,
                        usuarioRepository))
                .setViewResolvers(
                        new InternalResourceViewResolver("/vistas/", ".html"))
                .build();

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        lenient().when(usuarioRepository.findByEmail("estudiante@javali.com"))
                .thenReturn(Optional.of(usuario));
    } // Prepara el controller con un estudiante autenticado simulado.

    @Test
    void unaLeccionBloqueadaVuelveALaListaDeLeccionesConUnAviso()
            throws Exception {
        when(leccionService.obtener(5L)).thenReturn(
                new LeccionResponse(5L, 2L, "Ciclos", "Explicación", 2));
        when(avanceService.leccionDesbloqueada(1L, 5L)).thenReturn(false);

        mockMvc.perform(
                        get("/web/lecciones/5/ejercicios")
                                .principal(autenticacion))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/unidades/2/lecciones"))
                .andExpect(flash().attributeExists("avisoBloqueo"));
    }

    @Test
    void unaLeccionDesbloqueadaMuestraSusEjercicios() throws Exception {
        when(leccionService.obtener(5L)).thenReturn(
                new LeccionResponse(5L, 2L, "Ciclos", "Explicación", 2));
        when(avanceService.leccionDesbloqueada(1L, 5L)).thenReturn(true);
        when(ejercicioService.listarPorLeccion(5L)).thenReturn(List.of());

        mockMvc.perform(
                        get("/web/lecciones/5/ejercicios")
                                .principal(autenticacion))
                .andExpect(status().isOk())
                .andExpect(view().name("ejercicios"))
                .andExpect(model().attributeExists("leccion", "ejercicios"));
    }

    @Test
    void sinSesionLosCursosSeMuestranSinConsultarElAvance()
            throws Exception {
        when(cursoService.listar()).thenReturn(List.of());

        mockMvc.perform(get("/web/cursos"))
                .andExpect(status().isOk())
                .andExpect(view().name("cursos"))
                .andExpect(model().attributeExists("cursos", "avance"));

        verifyNoInteractions(avanceService);
    }
}
