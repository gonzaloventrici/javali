package com.javali.controllers.web;

import com.javali.dtos.PreferenciasRequest;
import com.javali.dtos.PreferenciasResponse;
import com.javali.models.NivelContraste;
import com.javali.models.TamanoLetra;
import com.javali.models.Tipografia;
import com.javali.models.Usuario;
import com.javali.repositories.UsuarioRepository;
import com.javali.services.PreferenciasService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class PreferenciasWebControllerTest {

    @Mock
    private PreferenciasService preferenciasService;

    @Mock
    private UsuarioRepository usuarioRepository;

    private MockMvc mockMvc;

    private final UsernamePasswordAuthenticationToken autenticacion =
            new UsernamePasswordAuthenticationToken(
                    "estudiante@javali.com", "x");

    @BeforeEach
    void preparar() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new PreferenciasWebController(
                        preferenciasService, usuarioRepository))
                .setViewResolvers(
                        new InternalResourceViewResolver("/vistas/", ".html"))
                .build();

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        when(usuarioRepository.findByEmail("estudiante@javali.com"))
                .thenReturn(Optional.of(usuario));
    } // Prepara el controller con un usuario autenticado simulado.

    @Test
    void mostrarCargaElFormularioConLosAjustesActuales() throws Exception {
        when(preferenciasService.obtener(1L))
                .thenReturn(PreferenciasResponse.porDefecto());

        mockMvc.perform(get("/web/preferencias").principal(autenticacion))
                .andExpect(status().isOk())
                .andExpect(view().name("preferencias"))
                .andExpect(model().attributeExists(
                        "formulario", "tamanos", "contrastes", "tipografias"));
    }

    @Test
    void guardarEnviaLosAjustesAlServicioYRedirige() throws Exception {
        mockMvc.perform(
                        post("/web/preferencias")
                                .principal(autenticacion)
                                .param("tamanoLetra", "GRANDE")
                                .param("contraste", "ALTO")
                                .param("tipografia", "DISLEXIA")
                                .param("mostrarRacha", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/preferencias"))
                .andExpect(flash().attribute("guardado", true));

        verify(preferenciasService).guardar(
                1L,
                new PreferenciasRequest(
                        TamanoLetra.GRANDE,
                        NivelContraste.ALTO,
                        Tipografia.DISLEXIA,
                        true));
    }

    @Test
    void sinMarcarLaRachaSeGuardaComoDesactivada() throws Exception {
        mockMvc.perform(
                        post("/web/preferencias")
                                .principal(autenticacion)
                                .param("tamanoLetra", "NORMAL")
                                .param("contraste", "NORMAL")
                                .param("tipografia", "SISTEMA"))
                .andExpect(status().is3xxRedirection());

        verify(preferenciasService).guardar(
                1L,
                new PreferenciasRequest(
                        TamanoLetra.NORMAL,
                        NivelContraste.NORMAL,
                        Tipografia.SISTEMA,
                        false));
    }
}
