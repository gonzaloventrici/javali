
// El service recibe el repositorio por constructor, y Spring se lo inyecta. 
// Depende de la interfaz UnidadRepository, no de su implementación.

//Implementa la interfaz UnidadService y contiene la lógica de negocio relacionada con las unidades.
//Utiliza UnidadRepository para acceder a los datos almacenados en la base de datos.
//Permite listar todas las unidades ordenadas por el campo orden.
//Permite obtener una unidad por su id y lanza UnidadNoEncontradaException si no existe.
//Permite crear nuevas unidades a partir de un UnidadRequest.
//Convierte las entidades Unidad en objetos UnidadResponse antes de devolverlas a la API.
//Utiliza anotaciones @Transactional para gestionar las transacciones de lectura y escritura.
//Mantiene separadas las capas de persistencia, negocio y presentación mediante el uso de DTOs.
package com.javali.services.impl;

import com.javali.dtos.UnidadRequest;
import com.javali.dtos.UnidadResponse;
import com.javali.exceptions.UnidadNoEncontradaException;
import com.javali.models.Unidad;
import com.javali.repositories.UnidadRepository;
import com.javali.services.UnidadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.javali.exceptions.CursoNoEncontradoException;
import com.javali.models.Curso;
import com.javali.repositories.CursoRepository;

import java.util.List;

@Service 
public class UnidadServiceImpl implements UnidadService {
    private final UnidadRepository unidadRepository;
    private final CursoRepository cursoRepository;

    public UnidadServiceImpl(
        UnidadRepository unidadRepository,
        CursoRepository cursoRepository) {
        this.unidadRepository = unidadRepository;
        this.cursoRepository = cursoRepository;
        }

    @Override
    @Transactional(readOnly = true) //define la transacción en el service, no en el repositorio.
    public List<UnidadResponse> listar() {
        return unidadRepository.findAllByOrderByOrdenAsc().stream()
                .map(this::aRespuesta)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnidadResponse> listarPorCurso(Long cursoId) {

        if (!cursoRepository.existsById(cursoId)) {
            throw new CursoNoEncontradoException(cursoId);
        }
        return unidadRepository
                .findByCurso_IdOrderByOrdenAsc(cursoId)
                .stream()
                .map(this::aRespuesta)
                .toList();
    }
    // Devuelve las unidades pertenecientes al curso solicitado.

    @Override
    @Transactional(readOnly = true)
    public UnidadResponse obtener(Long id) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new UnidadNoEncontradaException(id));
        return aRespuesta(unidad);
    }//El service devuelve DTOs, no entidades. Así la API no queda atada a la tabla.

    @Override
    @Transactional
    public UnidadResponse crear(
            Long cursoId,
            UnidadRequest request) {
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(
                        () -> new CursoNoEncontradoException(cursoId)
                );
        Unidad unidad = new Unidad();

        unidad.setCurso(curso);
        unidad.setTitulo(request.titulo());
        unidad.setDescripcion(request.description());
        unidad.setOrden(request.orden());

        return aRespuesta(unidadRepository.save(unidad));
    }
    // Crea una unidad y la relaciona con un curso existente.

    private UnidadResponse aRespuesta(Unidad unidad) {
        Long cursoId = unidad.getCurso() == null
                ? null
                : unidad.getCurso().getId();

        return new UnidadResponse(
                unidad.getId(),
                cursoId,
                unidad.getTitulo(),
                unidad.getDescripcion(),
                unidad.getOrden()
        );
    }
    // Convierte la unidad en DTO y admite unidades anteriores sin curso.
}
