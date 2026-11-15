package com.javali.controllers;

import tools.jackson.databind.ObjectMapper;
import com.javali.dtos.UsuarioRequest;
import com.javali.dtos.UsuarioResponse;
import com.javali.exceptions.EmailYaRegistradoException;
import com.javali.exceptions.ManejadorGlobalExcepciones;
import com.javali.models.RolUsuario;
import com.javali.services.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void preparar() {
        UsuarioController usuarioController =
                new UsuarioController(usuarioService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(usuarioController)
                .setControllerAdvice(
                        new ManejadorGlobalExcepciones()
                ).build();

        objectMapper = new ObjectMapper();
    } // Prepara el controller y el manejador de errores simulados.

    @Test
    void registrarUsuarioDevuelve201() throws Exception {
        UsuarioRequest request = new UsuarioRequest(
                "Gonzalo",
                "gonzalo@javali.com",
                "ClaveSegura123"
        );
        UsuarioResponse response = new UsuarioResponse(
                1L,
                "Gonzalo",
                "gonzalo@javali.com",
                RolUsuario.ESTUDIANTE
        );
        when(usuarioService.registrar(request))
                .thenReturn(response);

        mockMvc.perform(
                post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isCreated())
                .andExpect(
                        header().string(
                                "Location",
                                "/api/usuarios/1"
                        )
                )
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Gonzalo"))
                .andExpect(
                        jsonPath("$.email")
                                .value("gonzalo@javali.com")
                )
                .andExpect(
                        jsonPath("$.rol")
                                .value("ESTUDIANTE")
                )
                .andExpect(
                        jsonPath("$.password").doesNotExist()
                )
                .andExpect(
                        jsonPath("$.passwordHash").doesNotExist()
                );
    } // Comprueba el registro y que la contraseña no aparezca en el JSON.

    @Test
    void obtenerUsuarioDevuelve200() throws Exception {
        UsuarioResponse response = new UsuarioResponse(
                1L,
                "Gonzalo",
                "gonzalo@javali.com",
                RolUsuario.ESTUDIANTE
        );
        when(usuarioService.obtener(1L))
                .thenReturn(response);

        mockMvc.perform(
                get("/api/usuarios/1")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Gonzalo"))
                .andExpect(
                        jsonPath("$.email")
                                .value("gonzalo@javali.com")
                );
    } // Comprueba la consulta de un usuario existente.

    @Test
    void registrarEmailRepetidoDevuelve409() throws Exception {
        UsuarioRequest request = new UsuarioRequest(
                "Otro usuario",
                "gonzalo@javali.com",
                "OtraClave123"
        );
        when(usuarioService.registrar(request))
                .thenThrow(
                        new EmailYaRegistradoException(
                                "gonzalo@javali.com"
                        )
                );
        mockMvc.perform(
                post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isConflict())
                .andExpect(
                        jsonPath("$.title")
                                .value("Email ya registrado")
                )
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(
                        jsonPath("$.detail")
                                .value(
                                        "Ya existe un usuario con el email "
                                                + "gonzalo@javali.com"
                                )
                );
    } // Comprueba que un email repetido produzca 409 Conflict.

    @Test
    void registrarUsuarioConDatosInvalidosDevuelve400()
            throws Exception {
        String jsonInvalido = """
                {
                "nombre": "",
                "email": "correo-invalido",
                "password": "123"
                }
                """;
        mockMvc.perform(
                post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido)
        )
                .andExpect(status().isBadRequest());
    } // Comprueba que nombre, email y contraseña inválidos devuelvan 400.
}