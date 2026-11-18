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
import com.javali.models.TipoEjercicio;
import com.javali.services.ProgresoService;
import com.javali.validators.EjercicioValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
//-----------------------------------------------
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
// Permiten ejecutar la misma prueba con distintas categorías.

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
                progresoService,
                new EjercicioValidator()
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
        ejercicio.setTipo(TipoEjercicio.OPCIONES);
        ejercicio.setOpciones(
                new ArrayList<>(List.of("A", "B", "C", "D"))
        );
        ejercicio.setIndiceCorrecto(indiceCorrecto);

        return ejercicio;
    } // Crea un ejercicio mínimo reutilizable para las pruebas.

    @ParameterizedTest
    @EnumSource(
            value = CategoriaError.class,
            names = {
                "COMPILACION",        
                "LOGICA",
                "CONCEPTO"
                }
        )
        void corregirRespuestaIncorrectaConservaCategoriaDelError(
                CategoriaError categoriaError) {

        Long ejercicioId = 1L;
        Long usuarioId = 1L;
        Integer indiceSeleccionado = 0;

        Ejercicio ejercicio = crearEjercicio(
                ejercicioId,
                1
        );

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        RetroalimentacionOpcion retroalimentacion =
                new RetroalimentacionOpcion();

        retroalimentacion.setEjercicio(ejercicio);
        retroalimentacion.setIndiceOpcion(indiceSeleccionado);
        retroalimentacion.setCategoriaError(categoriaError);
        retroalimentacion.setExplicacion(
                "Explicación específica del error."
        );
        retroalimentacion.setPista("Pista específica para corregirlo.");

        RespuestaDTO respuesta = new RespuestaDTO(
                usuarioId,
                indiceSeleccionado
        );

        when(ejercicioRepository.findById(ejercicioId))
                .thenReturn(Optional.of(ejercicio));
        when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.of(usuario));
        when(retroalimentacionRepository
                .findByEjercicio_IdAndIndiceOpcion(
                        ejercicioId,
                        indiceSeleccionado
                ))
                .thenReturn(Optional.of(retroalimentacion));

        ResultadoCorreccionDTO resultado =
                ejercicioService.corregir(
                        ejercicioId,
                        respuesta
                );
        assertFalse(resultado.correcta());
        assertEquals(categoriaError, resultado.categoriaError());
        assertEquals("Explicación específica del error.", resultado.explicacion());
        assertEquals("Pista específica para corregirlo.", resultado.pista());

        ArgumentCaptor<Intento> captor =
                ArgumentCaptor.forClass(Intento.class);
        verify(intentoRepository).save(captor.capture());

        Intento intentoGuardado = captor.getValue();

        assertFalse(intentoGuardado.isCorrecta());
        assertEquals(categoriaError, intentoGuardado.getCategoriaError());

        verify(progresoService).actualizar(
                usuario,
                ejercicio,
                false
        );
    } // Verifica COMPILACION, LOGICA y CONCEPTO con la misma prueba.

    // ---------- Ejercicios de ordenar y de encontrar el error ----------

    private Ejercicio crearEjercicioOrdenar(Long id) {
        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setId(id);
        ejercicio.setTipo(TipoEjercicio.ORDENAR);
        ejercicio.setEnunciado("Ordená las líneas.");
        ejercicio.setExplicacion("Primero se declara, después se usa.");
        ejercicio.setPista("La variable va primero.");
        ejercicio.setOpciones(
                new ArrayList<>(List.of("}", "if (x) {", "int x = 1;"))
        );
        ejercicio.setOrdenCorrecto(new ArrayList<>(List.of(2, 1, 0)));
        return ejercicio;
    } // Crea un ejercicio de ordenar con tres líneas.

    @Test
    void corregirOrdenCorrectoRegistraIntentoConLaSecuencia() {
        Ejercicio ejercicio = crearEjercicioOrdenar(5L);
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        when(ejercicioRepository.findById(5L))
                .thenReturn(Optional.of(ejercicio));
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        ResultadoCorreccionDTO resultado = ejercicioService.corregir(
                5L,
                new RespuestaDTO(1L, null, List.of(2, 1, 0))
        );

        assertTrue(resultado.correcta());
        assertNull(resultado.categoriaError());

        ArgumentCaptor<Intento> captor =
                ArgumentCaptor.forClass(Intento.class);
        verify(intentoRepository).save(captor.capture());
        assertTrue(captor.getValue().isCorrecta());
        assertNull(captor.getValue().getIndiceSeleccionado());
        assertEquals("2,1,0", captor.getValue().getOrdenSeleccionado());
        verify(progresoService).actualizar(usuario, ejercicio, true);
    } // El orden correcto se guarda como texto en el intento.

    @Test
    void corregirOrdenIncorrectoDevuelveErrorDeLogica() {
        Ejercicio ejercicio = crearEjercicioOrdenar(5L);
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        when(ejercicioRepository.findById(5L))
                .thenReturn(Optional.of(ejercicio));
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        ResultadoCorreccionDTO resultado = ejercicioService.corregir(
                5L,
                new RespuestaDTO(1L, null, List.of(0, 1, 2))
        );

        assertFalse(resultado.correcta());
        assertEquals(CategoriaError.LOGICA, resultado.categoriaError());
        assertEquals("La variable va primero.", resultado.pista());

        ArgumentCaptor<Intento> captor =
                ArgumentCaptor.forClass(Intento.class);
        verify(intentoRepository).save(captor.capture());
        assertEquals(CategoriaError.LOGICA, captor.getValue().getCategoriaError());
    } // Un orden incorrecto se clasifica como error de lógica.

    @Test
    void corregirOrdenIncompletoLanzaExcepcionYNoRegistraIntento() {
        Ejercicio ejercicio = crearEjercicioOrdenar(5L);
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        when(ejercicioRepository.findById(5L))
                .thenReturn(Optional.of(ejercicio));
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        assertThrows(
                IllegalArgumentException.class,
                () -> ejercicioService.corregir(
                        5L,
                        new RespuestaDTO(1L, null, List.of(0, 1))
                )
        );
        verify(intentoRepository, never()).save(any(Intento.class));
    } // Si faltan líneas, no se guarda nada.

    @Test
    void corregirEncontrarErrorUsaLaRetroalimentacionDeLaLinea() {
        Ejercicio ejercicio = crearEjercicio(7L, 1);
        ejercicio.setTipo(TipoEjercicio.ENCONTRAR_ERROR);
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        RetroalimentacionOpcion retroalimentacion =
                new RetroalimentacionOpcion();
        retroalimentacion.setEjercicio(ejercicio);
        retroalimentacion.setIndiceOpcion(0);
        retroalimentacion.setCategoriaError(CategoriaError.CONCEPTO);
        retroalimentacion.setExplicacion("Esa línea está bien escrita.");
        retroalimentacion.setPista("Buscá la que no termina con ;");

        when(ejercicioRepository.findById(7L))
                .thenReturn(Optional.of(ejercicio));
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));
        when(retroalimentacionRepository
                .findByEjercicio_IdAndIndiceOpcion(7L, 0))
                .thenReturn(Optional.of(retroalimentacion));

        ResultadoCorreccionDTO resultado = ejercicioService.corregir(
                7L,
                new RespuestaDTO(1L, 0)
        );

        assertFalse(resultado.correcta());
        assertEquals(CategoriaError.CONCEPTO, resultado.categoriaError());
        assertEquals("Esa línea está bien escrita.", resultado.explicacion());
    } // Encontrar el error reutiliza la retroalimentación por opción.

    @Test
    void corregirConOpcionFueraDeRangoLanzaExcepcion() {
        Ejercicio ejercicio = crearEjercicio(1L, 1);
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        when(ejercicioRepository.findById(1L))
                .thenReturn(Optional.of(ejercicio));
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        assertThrows(
                IllegalArgumentException.class,
                () -> ejercicioService.corregir(
                        1L,
                        new RespuestaDTO(1L, 9)
                )
        );
    } // Una opción inexistente se rechaza antes de corregir.
}