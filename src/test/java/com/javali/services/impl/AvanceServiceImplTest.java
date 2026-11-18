package com.javali.services.impl;

import com.javali.dtos.EstadoAvance;
import com.javali.dtos.EstadoCursoResponse;
import com.javali.dtos.EstadoLeccionResponse;
import com.javali.dtos.EstadoUnidadResponse;
import com.javali.exceptions.CursoNoEncontradoException;
import com.javali.models.Curso;
import com.javali.models.Leccion;
import com.javali.models.Progreso;
import com.javali.models.RolUsuario;
import com.javali.models.Unidad;
import com.javali.models.Usuario;
import com.javali.repositories.CursoRepository;
import com.javali.repositories.LeccionRepository;
import com.javali.repositories.ProgresoRepository;
import com.javali.repositories.UnidadRepository;
import com.javali.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;

// Curso de prueba: unidad 10 (lecciones 100 y 101) y unidad 20 (lecciones 200 y 201).
@ExtendWith(MockitoExtension.class)
class AvanceServiceImplTest {

    private static final Long USUARIO = 1L;
    private static final Long CURSO = 1L;

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private CursoRepository cursoRepository;
    @Mock private UnidadRepository unidadRepository;
    @Mock private LeccionRepository leccionRepository;
    @Mock private ProgresoRepository progresoRepository;

    private AvanceServiceImpl servicio;

    private final Curso curso = new Curso();
    private final List<Unidad> unidades = new ArrayList<>();
    private final Map<Long, Leccion> lecciones = new HashMap<>();
    private final Map<Long, List<Leccion>> leccionesPorUnidad = new HashMap<>();
    private final List<Progreso> progresos = new ArrayList<>();

    @BeforeEach
    void preparar() {
        servicio = new AvanceServiceImpl(
                usuarioRepository,
                cursoRepository,
                unidadRepository,
                leccionRepository,
                progresoRepository
        );

        curso.setId(CURSO);
        unidades.add(crearUnidad(10L, 1));
        unidades.add(crearUnidad(20L, 2));
        crearLeccion(100L, unidades.get(0), 1);
        crearLeccion(101L, unidades.get(0), 2);
        crearLeccion(200L, unidades.get(1), 1);
        crearLeccion(201L, unidades.get(1), 2);

        usarUsuario(RolUsuario.ESTUDIANTE);
        lenient().when(cursoRepository.existsById(CURSO)).thenReturn(true);
        lenient().when(cursoRepository.findAll()).thenReturn(List.of(curso));
        lenient().when(unidadRepository.findByCurso_IdOrderByOrdenAsc(CURSO))
                .thenReturn(unidades);
        lenient().when(progresoRepository
                        .findByUsuario_IdOrderByUltimaActividadDesc(USUARIO))
                .thenReturn(progresos);
    } // Arma el curso de prueba con repositorios simulados.

    private Unidad crearUnidad(Long id, int orden) {
        Unidad unidad = new Unidad();
        unidad.setId(id);
        unidad.setCurso(curso);
        unidad.setTitulo("Unidad " + id);
        unidad.setOrden(orden);
        List<Leccion> propias = new ArrayList<>();
        leccionesPorUnidad.put(id, propias);
        lenient().when(unidadRepository.findById(id))
                .thenReturn(Optional.of(unidad));
        lenient().when(leccionRepository.findByUnidad_IdOrderByOrdenAsc(id))
                .thenReturn(propias);
        return unidad;
    }

    private void crearLeccion(Long id, Unidad unidad, int orden) {
        Leccion leccion = new Leccion();
        leccion.setId(id);
        leccion.setUnidadId(unidad);
        leccion.setTitulo("Lección " + id);
        leccion.setOrden(orden);
        lecciones.put(id, leccion);
        leccionesPorUnidad.get(unidad.getId()).add(leccion);
        lenient().when(leccionRepository.findById(id))
                .thenReturn(Optional.of(leccion));
    }

    private void usarUsuario(RolUsuario rol) {
        Usuario usuario = new Usuario();
        usuario.setId(USUARIO);
        usuario.setRol(rol);
        lenient().when(usuarioRepository.findById(USUARIO))
                .thenReturn(Optional.of(usuario));
    }

    private void completar(Long leccionId) {
        Progreso progreso = new Progreso();
        progreso.setLeccion(lecciones.get(leccionId));
        progreso.setCompletada(true);
        progreso.setPorcentajeAciertos(100);
        progresos.add(progreso);
    }

    @Test
    void sinProgresoSoloLaPrimeraUnidadEstaEnCurso() {
        Map<Long, EstadoUnidadResponse> estados =
                servicio.estadoUnidades(USUARIO, CURSO);

        assertEquals(EstadoAvance.EN_CURSO, estados.get(10L).estado());
        assertEquals(EstadoAvance.BLOQUEADA, estados.get(20L).estado());
    } // Las unidades siguientes se ven pero aparecen bloqueadas.

    @Test
    void soloLaPrimeraLeccionDelCursoEstaDesbloqueadaAlEmpezar() {
        Map<Long, EstadoLeccionResponse> unidad1 =
                servicio.estadoLecciones(USUARIO, 10L);
        Map<Long, EstadoLeccionResponse> unidad2 =
                servicio.estadoLecciones(USUARIO, 20L);

        assertTrue(unidad1.get(100L).desbloqueada());
        assertFalse(unidad1.get(101L).desbloqueada());
        assertFalse(unidad2.get(200L).desbloqueada());
    }

    @Test
    void completarUnaUnidadDesbloqueaLaSiguiente() {
        completar(100L);
        completar(101L);

        Map<Long, EstadoUnidadResponse> estados =
                servicio.estadoUnidades(USUARIO, CURSO);
        Map<Long, EstadoLeccionResponse> unidad2 =
                servicio.estadoLecciones(USUARIO, 20L);

        assertEquals(EstadoAvance.COMPLETADA, estados.get(10L).estado());
        assertEquals(EstadoAvance.EN_CURSO, estados.get(20L).estado());
        assertTrue(unidad2.get(200L).desbloqueada());
        assertFalse(unidad2.get(201L).desbloqueada());
    } // La primera lección de la unidad 2 se abre al terminar la unidad 1.

    @Test
    void elCursoSeMarcaCompletadoCuandoSeCompletaTodo() {
        completar(100L);
        completar(101L);
        completar(200L);
        completar(201L);

        EstadoCursoResponse estado = servicio.estadoCurso(USUARIO, CURSO);

        assertTrue(estado.completado());
        assertEquals(2, estado.unidadesCompletadas());
        assertEquals(4, estado.leccionesCompletadas());
        assertTrue(servicio.estadoCursos(USUARIO).get(CURSO).completado());
    }

    @Test
    void conAvanceParcialElCursoNoEstaCompletado() {
        completar(100L);

        EstadoCursoResponse estado = servicio.estadoCurso(USUARIO, CURSO);

        assertFalse(estado.completado());
        assertEquals(1, estado.leccionesCompletadas());
        assertEquals(4, estado.leccionesTotales());
        assertEquals(0, estado.unidadesCompletadas());
        assertEquals(2, estado.unidadesTotales());
    }

    @Test
    void unaUnidadSinLeccionesEsProximamenteYNoFrenaElCurso() {
        unidades.add(crearUnidad(30L, 3));
        completar(100L);
        completar(101L);
        completar(200L);
        completar(201L);

        Map<Long, EstadoUnidadResponse> estados =
                servicio.estadoUnidades(USUARIO, CURSO);
        EstadoCursoResponse estado = servicio.estadoCurso(USUARIO, CURSO);

        assertEquals(EstadoAvance.PROXIMAMENTE, estados.get(30L).estado());
        assertEquals(2, estado.unidadesTotales());
        assertTrue(estado.completado());
    } // Una unidad vacía se muestra como "próximamente" y no cuenta.

    @Test
    void unAdministradorTieneTodoDesbloqueado() {
        usarUsuario(RolUsuario.ADMINISTRADOR);

        Map<Long, EstadoUnidadResponse> estados =
                servicio.estadoUnidades(USUARIO, CURSO);

        assertEquals(EstadoAvance.EN_CURSO, estados.get(20L).estado());
        assertTrue(servicio.leccionDesbloqueada(USUARIO, 201L));
    }

    @Test
    void leccionDesbloqueadaDependeDeLaLeccionAnterior() {
        assertTrue(servicio.leccionDesbloqueada(USUARIO, 100L));
        assertFalse(servicio.leccionDesbloqueada(USUARIO, 201L));
    }

    @Test
    void unCursoInexistenteLanzaExcepcion() {
        assertThrows(
                CursoNoEncontradoException.class,
                () -> servicio.estadoUnidades(USUARIO, 99L)
        );
    }
}
