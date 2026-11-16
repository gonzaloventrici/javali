package com.javali.services.impl;

import com.javali.dtos.EjercicioRequest;
import com.javali.dtos.EjercicioResponse;
import com.javali.exceptions.EjercicioNoEncontradoException;
import com.javali.exceptions.LeccionNoEncontradaException;
import com.javali.models.Ejercicio;
import com.javali.models.Leccion;
import com.javali.repositories.EjercicioRepository;
import com.javali.repositories.LeccionRepository;
import com.javali.services.EjercicioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.javali.dtos.RespuestaDTO;
import com.javali.dtos.ResultadoCorreccionDTO;
import com.javali.models.RetroalimentacionOpcion;
import com.javali.repositories.RetroalimentacionOpcionRepository;
import com.javali.exceptions.UsuarioNoEncontradoException;
import com.javali.models.Intento;
import com.javali.models.Usuario;
import com.javali.repositories.IntentoRepository;
import com.javali.repositories.UsuarioRepository;
import com.javali.services.ProgresoService;
import com.javali.exceptions.RecursoConDependenciasException;


import java.time.LocalDateTime;


import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

@Service
public class EjercicioServiceImpl implements EjercicioService {

    private final EjercicioRepository ejercicioRepository;
    private final LeccionRepository leccionRepository;
    private final RetroalimentacionOpcionRepository retroalimentacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final IntentoRepository intentoRepository;
    private final ProgresoService progresoService;
    // Permiten buscar al usuario y guardar su intento.


    public EjercicioServiceImpl(
        EjercicioRepository ejercicioRepository,
        LeccionRepository leccionRepository,
        RetroalimentacionOpcionRepository retroalimentacionRepository,
        UsuarioRepository usuarioRepository,
        IntentoRepository intentoRepository,
        ProgresoService progresoService) {

        this.ejercicioRepository = ejercicioRepository;
        this.leccionRepository = leccionRepository;
        this.retroalimentacionRepository = retroalimentacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.intentoRepository = intentoRepository;
        this.progresoService = progresoService;
        }

        // Recibe todos los repositorios mediante inyección por constructor.

    @Override
    @Transactional(readOnly = true)
    public List<EjercicioResponse> listarPorLeccion(Long leccionId) {
        if (!leccionRepository.existsById(leccionId)) {
            throw new LeccionNoEncontradaException(leccionId);
        }
        return ejercicioRepository
        .findByLeccion_IdOrderByOrdenAsc(leccionId)
        .stream()
        .map(this::aRespuesta)
        .toList();
        // Obtiene los ejercicios de la lección y los convierte en DTO.
    }
    // Verifica la lección y devuelve sus ejercicios ordenados.

    @Override
    @Transactional(readOnly = true)
    public EjercicioResponse obtener(Long id) {
        Ejercicio ejercicio = ejercicioRepository.findById(id)
                .orElseThrow(
                        () -> new EjercicioNoEncontradoException(id)
                );
        return aRespuesta(ejercicio);
    }
    // Busca un ejercicio por id o lanza una excepción.

        @Override
        @Transactional
        public EjercicioResponse crear(
                Long leccionId,
                EjercicioRequest request) {

        Leccion leccion = leccionRepository.findById(leccionId)
                .orElseThrow(
                        () -> new LeccionNoEncontradaException(leccionId)
                );

        validarIndiceCorrecto(
                request.indiceCorrecto(),
                request.opciones()
        );

        Ejercicio ejercicio = new Ejercicio();

        ejercicio.setLeccion(leccion);
        ejercicio.setTipo(request.tipo());
        ejercicio.setEnunciado(request.enunciado().trim());
        ejercicio.setCodigo(
                request.codigo() == null
                        ? null
                        : request.codigo().trim()
        );
        ejercicio.setExplicacion(
                request.explicacion().trim()
        );
        ejercicio.setPista(
                request.pista() == null
                        ? null
                        : request.pista().trim()
        );
        ejercicio.setOrden(request.orden());
        ejercicio.setOpciones(
                new ArrayList<>(request.opciones())
        );
        ejercicio.setIndiceCorrecto(
                request.indiceCorrecto()
        );
        return aRespuesta(
                ejercicioRepository.save(ejercicio)
        );
        }

    @Override
        @Transactional
        public ResultadoCorreccionDTO corregir(
                Long id,
                RespuestaDTO respuesta) {

        Ejercicio ejercicio = ejercicioRepository.findById(id)
                .orElseThrow(
                        () -> new EjercicioNoEncontradoException(id)
                );

        Usuario usuario = usuarioRepository
                .findById(respuesta.usuarioId())
                .orElseThrow(
                        () -> new UsuarioNoEncontradoException(
                                respuesta.usuarioId()
                        )
                );

        boolean correcta = Objects.equals(
                ejercicio.getIndiceCorrecto(),
                respuesta.indiceSeleccionado()
        );

        RetroalimentacionOpcion retroalimentacion = null;

        if (!correcta) {
                retroalimentacion = retroalimentacionRepository
                        .findByEjercicio_IdAndIndiceOpcion(
                                id,
                                respuesta.indiceSeleccionado()
                        )
                        .orElse(null);
        }

        Intento intento = new Intento();

        intento.setUsuario(usuario);
        intento.setEjercicio(ejercicio);
        intento.setIndiceSeleccionado(
                respuesta.indiceSeleccionado()
        );
        intento.setCorrecta(correcta);
        intento.setCategoriaError(
                retroalimentacion == null
                        ? null
                        : retroalimentacion.getCategoriaError()
        );
        intento.setFecha(LocalDateTime.now());

        intentoRepository.save(intento);
        
        progresoService.actualizar(
        usuario,
        ejercicio,
        correcta);
        // Actualiza el progreso del usuario en la lección.

        if (correcta) {
                return new ResultadoCorreccionDTO(
                        true,
                        null,
                        ejercicio.getExplicacion(),
                        null
                );
        }

        if (retroalimentacion == null) {
                return new ResultadoCorreccionDTO(
                        false,
                        null,
                        ejercicio.getExplicacion(),
                        ejercicio.getPista()
                );
        }

        return new ResultadoCorreccionDTO(
                false,
                retroalimentacion.getCategoriaError(),
                retroalimentacion.getExplicacion(),
                retroalimentacion.getPista()
        );
        }

        // Corrige la respuesta y registra el intento del usuario.
        // Corrige la respuesta y devuelve una devolución específica si existe.
        // Compara la opción elegida con la correcta y devuelve el resultado.

    private EjercicioResponse aRespuesta(
        Ejercicio ejercicio) {
        return new EjercicioResponse(
                ejercicio.getId(),
                ejercicio.getLeccion().getId(),
                ejercicio.getTipo(),
                ejercicio.getEnunciado(),
                ejercicio.getCodigo(),
                ejercicio.getExplicacion(),
                ejercicio.getPista(),
                ejercicio.getOrden(),
                List.copyOf(ejercicio.getOpciones())
        );
        }
    // Convierte la entidad Ejercicio en un DTO sin la respuesta correcta.

    @Override
        @Transactional
        public EjercicioResponse modificar(
                Long id,
                EjercicioRequest request) {
        Ejercicio ejercicio = buscarEjercicio(id);
        validarIndiceCorrecto(
                request.indiceCorrecto(),
                request.opciones()
        );
        ejercicio.setTipo(request.tipo());
        ejercicio.setEnunciado(request.enunciado().trim());
        ejercicio.setCodigo(
                request.codigo() == null
                        ? null
                        : request.codigo().trim()
        );
        ejercicio.setExplicacion(
                request.explicacion().trim()
        );
        ejercicio.setPista(
                request.pista() == null
                        ? null
                        : request.pista().trim()
        );
        ejercicio.setOrden(request.orden());
        ejercicio.getOpciones().clear();
        ejercicio.getOpciones().addAll(
                request.opciones()
        );
        ejercicio.setIndiceCorrecto(
                request.indiceCorrecto()
        );
        return aRespuesta(ejercicio);
        }
        // Modifica el ejercicio sin cambiar la lección a la que pertenece.

        @Override
        @Transactional
        public void eliminar(Long id) {
        Ejercicio ejercicio = buscarEjercicio(id);
        if (intentoRepository.existsByEjercicio_Id(id)) {
                throw new RecursoConDependenciasException(
                        "No se puede eliminar el ejercicio porque tiene intentos registrados"
                );
                }
        if (retroalimentacionRepository
                .existsByEjercicio_Id(id)) {
                throw new RecursoConDependenciasException(
                        "No se puede eliminar el ejercicio porque tiene retroalimentaciones"
                );
                }
        ejercicioRepository.delete(ejercicio);
        }
        // Elimina un ejercicio solamente cuando no tiene historial asociado.

        //AUXILIARES
        private Ejercicio buscarEjercicio(Long id) {
                return ejercicioRepository.findById(id)
                        .orElseThrow(() -> new EjercicioNoEncontradoException(id));
                }
                // Busca un ejercicio o informa que no existe.
        
        private void validarIndiceCorrecto(
                Integer indiceCorrecto,
                List<String> opciones) {
        if (opciones == null || opciones.size() < 2) {
                throw new IllegalArgumentException(
                        "El ejercicio debe contener al menos dos opciones"
                );
                }
        if (indiceCorrecto == null
                || indiceCorrecto < 0
                || indiceCorrecto >= opciones.size()) {
                throw new IllegalArgumentException(
                        "El índice correcto debe corresponder a una opción existente"
                );
                }
        }
        // Comprueba que la respuesta correcta exista entre las opciones.
}