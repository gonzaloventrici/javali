package com.javali.services.impl;

import com.javali.dtos.CursoRequest;
import com.javali.dtos.CursoResponse;
import com.javali.exceptions.CursoNoEncontradoException;
import com.javali.models.Curso;
import com.javali.repositories.CursoRepository;
import com.javali.services.CursoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.javali.exceptions.RecursoConDependenciasException;
import com.javali.repositories.UnidadRepository;

import java.util.List;

@Service
public class CursoServiceImpl implements CursoService {

    private final CursoRepository cursoRepository;
    private final UnidadRepository unidadRepository;

    public CursoServiceImpl(
        CursoRepository cursoRepository,
        UnidadRepository unidadRepository) {
        this.cursoRepository = cursoRepository;
        this.unidadRepository = unidadRepository;
    }
    // Recibe el repositorio mediante inyección por constructor.

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponse> listar() {

        return cursoRepository
                .findAllByOrderByTituloAsc()
                .stream()
                .map(this::aRespuesta)
                .toList();
    }
    // Devuelve todos los cursos ordenados por título.

    @Override
    @Transactional(readOnly = true)
    public CursoResponse obtener(Long id) {

        Curso curso = cursoRepository.findById(id)
                .orElseThrow(
                        () -> new CursoNoEncontradoException(id)
                );

        return aRespuesta(curso);
    }
    // Busca un curso por id o informa que no existe.

    @Override
    @Transactional
    public CursoResponse crear(CursoRequest request) {

        Curso curso = new Curso();

        curso.setTitulo(request.titulo().trim());
        curso.setDescripcion(request.descripcion().trim());
        curso.setActivo(request.activo());

        return aRespuesta(cursoRepository.save(curso));
    }
    // Crea un curso con los datos recibidos.

    @Override
    @Transactional
    public CursoResponse modificar(
            Long id,
            CursoRequest request) {

        Curso curso = buscarCurso(id);

        curso.setTitulo(request.titulo().trim());
        curso.setDescripcion(request.descripcion().trim());
        curso.setActivo(request.activo());

        Curso cursoActualizado =
                cursoRepository.save(curso);

        return aRespuesta(cursoActualizado);
    }
    // Modifica los datos de un curso existente.

    @Override
    @Transactional
    public void eliminar(Long id) {

        Curso curso = buscarCurso(id);

        if (unidadRepository.existsByCurso_Id(id)) {
            throw new RecursoConDependenciasException(
                    "No se puede eliminar el curso porque contiene unidades"
            );
        }
        cursoRepository.delete(curso);
    }
    // Elimina un curso solamente si no contiene unidades.

    private CursoResponse aRespuesta(Curso curso) {
        return new CursoResponse(
                curso.getId(),
                curso.getTitulo(),
                curso.getDescripcion(),
                curso.isActivo()
        );
    }
    // Convierte una entidad Curso en un DTO de respuesta.

    //AUXILIARES
    private Curso buscarCurso(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(
                        () -> new CursoNoEncontradoException(id)
                );
    }
    // Busca un curso o informa que no existe.
}