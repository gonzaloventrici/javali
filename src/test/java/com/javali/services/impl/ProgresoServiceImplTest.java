package com.javali.services.impl;

import com.javali.dtos.ResumenProgresoResponse;
import com.javali.models.CategoriaError;
import com.javali.models.Intento;
import com.javali.models.Leccion;
import com.javali.models.Progreso;
import com.javali.repositories.IntentoRepository;
import com.javali.repositories.LeccionRepository;
import com.javali.repositories.ProgresoRepository;
import com.javali.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.javali.dtos.EstadoLeccionResponse;
import com.javali.models.Unidad;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgresoServiceImplTest {

    @Mock // Crea repositorios simulados sin consultar SQL Server.
    private ProgresoRepository progresoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private IntentoRepository intentoRepository;
    
    @Mock       
    private LeccionRepository leccionRepository;

    private ProgresoServiceImpl progresoService;

    @BeforeEach
    void preparar() {
        progresoService = new ProgresoServiceImpl(
                progresoRepository,
                usuarioRepository,
                intentoRepository,
                leccionRepository
        );
    }
    // Crea el servicio usando repositorios simulados.

    @Test
    void obtenerResumenSinIntentosDevuelveRecomendacionInicial() {
        Long usuarioId = 1L;
        when(usuarioRepository.existsById(usuarioId))
                .thenReturn(true); // Simula que el usuario existe.
        when(intentoRepository.findByUsuario_Id(usuarioId))
                .thenReturn(List.of());
        when(progresoRepository
                .findByUsuario_IdOrderByUltimaActividadDesc(usuarioId))
                .thenReturn(List.of());

        ResumenProgresoResponse resultado =
                progresoService.obtenerResumen(usuarioId);

        assertEquals(0, resultado.totalIntentos());
        assertEquals(0, resultado.respuestasCorrectas());
        assertEquals(0.0, resultado.porcentajeGeneral());
        assertNull(resultado.errorFrecuente());
        assertEquals(List.of(), resultado.fortalezas());
        assertEquals(List.of(), resultado.temasPendientes());

        assertEquals(
                "Realizar ejercicios para comenzar a medir el progreso.",
                resultado.recomendacion()
        );
    }
    // Comprueba el resumen de un usuario que todavía no respondió ejercicios.

    @Test
    void obtenerResumenClasificaFortalezasYTemasPendientes() {
        Long usuarioId = 1L;

        Intento intentoCorrecto = new Intento();
        intentoCorrecto.setCorrecta(true);

        Intento intentoIncorrecto = new Intento();
        intentoIncorrecto.setCorrecta(false);
        intentoIncorrecto.setCategoriaError(CategoriaError.SINTAXIS);

        Leccion leccionCompletada = new Leccion();
        leccionCompletada.setTitulo("Variables");

        Progreso progresoCompletado = new Progreso();
        progresoCompletado.setLeccion(leccionCompletada);
        progresoCompletado.setCompletada(true);

        Leccion leccionPendiente = new Leccion();
        leccionPendiente.setTitulo("Condicionales");

        Progreso progresoPendiente = new Progreso();
        progresoPendiente.setLeccion(leccionPendiente);
        progresoPendiente.setCompletada(false);

        when(usuarioRepository.existsById(usuarioId))
                .thenReturn(true);
        when(intentoRepository.findByUsuario_Id(usuarioId))
                .thenReturn(
                        List.of(
                                intentoCorrecto,
                                intentoIncorrecto
                        )
                );
        when(progresoRepository
                .findByUsuario_IdOrderByUltimaActividadDesc(usuarioId))
                .thenReturn(
                        List.of(
                                progresoCompletado,
                                progresoPendiente
                        )
                );

        ResumenProgresoResponse resultado =
                progresoService.obtenerResumen(usuarioId);

        assertEquals(2, resultado.totalIntentos());
        assertEquals(1, resultado.respuestasCorrectas());
        assertEquals(50.0, resultado.porcentajeGeneral());
        assertEquals(CategoriaError.SINTAXIS, resultado.errorFrecuente());

        assertEquals(List.of("Variables"), resultado.fortalezas());

        assertEquals(List.of("Condicionales"), resultado.temasPendientes());

        assertEquals("Practicar la escritura y estructura de código Java.", resultado.recomendacion());
    }
    // Comprueba porcentajes, fortalezas, pendientes y error frecuente.

    @Test
        void primeraLeccionEstaDesbloqueada() {

        Long usuarioId = 1L;
        Long leccionId = 1L;

        Unidad unidad = new Unidad();
        unidad.setId(1L);

        Leccion leccion = new Leccion();
        leccion.setId(leccionId);
        leccion.setUnidadId(unidad);
        leccion.setTitulo("Declaración de variables");
        leccion.setOrden(1);

        when(usuarioRepository.existsById(usuarioId))
                .thenReturn(true);
        when(leccionRepository.findById(leccionId))
                .thenReturn(Optional.of(leccion));
        when(progresoRepository
                .findByUsuario_IdAndLeccion_Id(
                        usuarioId,
                        leccionId
                ))
                .thenReturn(Optional.empty());
        when(leccionRepository
                .findFirstByUnidad_IdAndOrdenLessThanOrderByOrdenDesc(
                        unidad.getId(),
                        leccion.getOrden()
                ))
                .thenReturn(Optional.empty());

        EstadoLeccionResponse resultado =
                progresoService.obtenerEstadoLeccion(
                        usuarioId,
                        leccionId
                );

        assertEquals(true, resultado.desbloqueada());
        assertEquals(false, resultado.completada());
        assertEquals(0.0, resultado.porcentajeAciertos());
        } // Comprueba que la primera lección de una unidad esté desbloqueada.

        @Test
        void segundaLeccionEstaBloqueadaSiLaAnteriorNoFueCompletada() {

        Long usuarioId = 1L;

        Unidad unidad = new Unidad();
        unidad.setId(1L);

        Leccion anterior = new Leccion();
        anterior.setId(1L);
        anterior.setUnidadId(unidad);
        anterior.setTitulo("Declaración de variables");
        anterior.setOrden(1);

        Leccion actual = new Leccion();
        actual.setId(2L);
        actual.setUnidadId(unidad);
        actual.setTitulo("Tipos primitivos");
        actual.setOrden(2);

        Progreso progresoAnterior = new Progreso();
        progresoAnterior.setLeccion(anterior);
        progresoAnterior.setCompletada(false);
        progresoAnterior.setPorcentajeAciertos(50.0);

        when(usuarioRepository.existsById(usuarioId))
                .thenReturn(true);
        when(leccionRepository.findById(actual.getId()))
                .thenReturn(Optional.of(actual));
        when(progresoRepository
                .findByUsuario_IdAndLeccion_Id(
                        usuarioId,
                        actual.getId()
                ))
                .thenReturn(Optional.empty());
        when(leccionRepository
                .findFirstByUnidad_IdAndOrdenLessThanOrderByOrdenDesc(
                        unidad.getId(),
                        actual.getOrden()
                ))
                .thenReturn(Optional.of(anterior));
        when(progresoRepository
                .findByUsuario_IdAndLeccion_Id(
                        usuarioId,
                        anterior.getId()
                ))
                .thenReturn(Optional.of(progresoAnterior));

        EstadoLeccionResponse resultado =
                progresoService.obtenerEstadoLeccion(
                        usuarioId,
                        actual.getId()
                );
        assertEquals(false, resultado.desbloqueada());
        assertEquals(false, resultado.completada());
        } // Comprueba que una lección permanezca bloqueada si la anterior está incompleta.

        @Test
        void segundaLeccionSeDesbloqueaSiLaAnteriorFueCompletada() {

        Long usuarioId = 1L;

        Unidad unidad = new Unidad();
        unidad.setId(1L);

        Leccion anterior = new Leccion();
        anterior.setId(1L);
        anterior.setUnidadId(unidad);
        anterior.setTitulo("Declaración de variables");
        anterior.setOrden(1);

        Leccion actual = new Leccion();
        actual.setId(2L);
        actual.setUnidadId(unidad);
        actual.setTitulo("Tipos primitivos");
        actual.setOrden(2);

        Progreso progresoAnterior = new Progreso();
        progresoAnterior.setLeccion(anterior);
        progresoAnterior.setCompletada(true);
        progresoAnterior.setPorcentajeAciertos(80.0);

        when(usuarioRepository.existsById(usuarioId))
                .thenReturn(true);
        when(leccionRepository.findById(actual.getId()))
                .thenReturn(Optional.of(actual));
        when(progresoRepository
                .findByUsuario_IdAndLeccion_Id(
                        usuarioId,
                        actual.getId()
                ))
                .thenReturn(Optional.empty());
        when(leccionRepository
                .findFirstByUnidad_IdAndOrdenLessThanOrderByOrdenDesc(
                        unidad.getId(),
                        actual.getOrden()
                ))
                .thenReturn(Optional.of(anterior));
        when(progresoRepository
                .findByUsuario_IdAndLeccion_Id(
                        usuarioId,
                        anterior.getId()
                ))
                .thenReturn(Optional.of(progresoAnterior));

        EstadoLeccionResponse resultado =
                progresoService.obtenerEstadoLeccion(
                        usuarioId,
                        actual.getId()
                );

        assertEquals(true, resultado.desbloqueada());
        assertEquals(false, resultado.completada());
        } // Comprueba que completar una lección desbloquee la siguiente.
}