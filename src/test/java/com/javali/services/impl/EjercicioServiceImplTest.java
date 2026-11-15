package com.javali.services.impl;

import com.javali.dtos.RespuestaDTO;
import com.javali.dtos.ResultadoCorreccionDTO;
import com.javali.models.CategoriaError;
import com.javali.models.Ejercicio;
import com.javali.models.Intento;
import com.javali.models.RetroalimentacionOpcion;
import com.javali.models.Usuario;
import com.javali.repositories.EjercicioRepository;
import com.javali.repositories.IntentoRepository;
import com.javali.repositories.LeccionRepository;
import com.javali.repositories.RetroalimentacionOpcionRepository;
import com.javali.repositories.UsuarioRepository;
import com.javali.services.ProgresoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EjercicioServiceImplTest {

    @Mock
    private EjercicioRepository ejercicioRepository;

    @Mock
    private LeccionRepository leccionRepository;

    @Mock
    private RetroalimentacionOpcionRepository retroalimentacionRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private IntentoRepository intentoRepository;

    @Mock
    private ProgresoService progresoService;

    private EjercicioServiceImpl ejercicioService;

    @BeforeEach
    void preparar() {
        ejercicioService = new EjercicioServiceImpl(
                ejercicioRepository,
                leccionRepository,
                retroalimentacionRepository,
                usuarioRepository,
                intentoRepository,
                progresoService
        );
    } // Crea el servicio con repositorios y progreso simulados.

    @Test
    void corregirRespuestaCorrectaRegistraIntentoCorrecto() {

        Long ejercicioId = 1L;
        Long usuarioId = 1L;

        Ejercicio ejercicio = crearEjercicio(ejercicioId, 1);

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        RespuestaDTO respuesta =
                new RespuestaDTO(usuarioId, 1);

        when(ejercicioRepository.findById(ejercicioId))
                .thenReturn(Optional.of(ejercicio));
        when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.of(usuario));

        ResultadoCorreccionDTO resultado =
                ejercicioService.corregir(
                        ejercicioId,
                        respuesta
                );

        assertTrue(resultado.correcta());
        assertNull(resultado.categoriaError());
        assertNull(resultado.pista());

        ArgumentCaptor<Intento> captor =
                ArgumentCaptor.forClass(Intento.class);

        verify(intentoRepository).save(captor.capture());

        Intento intentoGuardado = captor.getValue();

        assertTrue(intentoGuardado.isCorrecta());
        assertEquals(1, intentoGuardado.getIndiceSeleccionado());
        assertNull(intentoGuardado.getCategoriaError());

        verify(progresoService).actualizar(
                usuario,
                ejercicio,
                true
        );
    } // Comprueba la respuesta correcta y el intento registrado.

    @Test
    void corregirRespuestaIncorrectaDevuelveErrorDeSintaxis() {

        Long ejercicioId = 1L;
        Long usuarioId = 1L;

        Ejercicio ejercicio = crearEjercicio(ejercicioId, 1);
        
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        RetroalimentacionOpcion retroalimentacion =
                new RetroalimentacionOpcion();

        retroalimentacion.setEjercicio(ejercicio);
        retroalimentacion.setIndiceOpcion(0);
        retroalimentacion.setCategoriaError(
                CategoriaError.SINTAXIS
        );
        retroalimentacion.setExplicacion(
                "El tipo debe escribirse antes del nombre."
        );
        retroalimentacion.setPista(
                "Revisá el orden de int y edad."
        );

        RespuestaDTO respuesta =
                new RespuestaDTO(usuarioId, 0);

        when(ejercicioRepository.findById(ejercicioId))
                .thenReturn(Optional.of(ejercicio));
        when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.of(usuario));
        when(retroalimentacionRepository
                .findByEjercicio_IdAndIndiceOpcion(
                        ejercicioId,
                        0
                ))
                .thenReturn(Optional.of(retroalimentacion));

        ResultadoCorreccionDTO resultado =
                ejercicioService.corregir(
                        ejercicioId,
                        respuesta
                );

        assertFalse(resultado.correcta());

        assertEquals(CategoriaError.SINTAXIS, resultado.categoriaError());
        assertEquals("El tipo debe escribirse antes del nombre.", resultado.explicacion());
        assertEquals("Revisá el orden de int y edad.", resultado.pista());

        ArgumentCaptor<Intento> captor =
                ArgumentCaptor.forClass(Intento.class);
        verify(intentoRepository).save(captor.capture());

        Intento intentoGuardado = captor.getValue();
        assertFalse(intentoGuardado.isCorrecta());

        assertEquals(CategoriaError.SINTAXIS, intentoGuardado.getCategoriaError());

        verify(progresoService).actualizar(
                usuario,
                ejercicio,
                false
        );
    } // Comprueba la clasificación de sintaxis y el intento incorrecto.

    private Ejercicio crearEjercicio(
            Long id,
            Integer indiceCorrecto) {

        Ejercicio ejercicio = new Ejercicio();

        ejercicio.setId(id);
        ejercicio.setEnunciado(
                "¿Cuál declaración es correcta?"
        );
        ejercicio.setExplicacion(
                "El tipo se escribe antes del nombre."
        );
        ejercicio.setPista(
                "Buscá la opción que comienza con int."
        );
        ejercicio.setIndiceCorrecto(indiceCorrecto);

        return ejercicio;
    } // Crea un ejercicio mínimo reutilizable para las pruebas.
}