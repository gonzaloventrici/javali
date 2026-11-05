
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
import java.util.List;

@Service 
public class UnidadServiceImpl implements UnidadService {
    private final UnidadRepository unidadRepository;

    public UnidadServiceImpl(UnidadRepository unidadRepository) {
        this.unidadRepository = unidadRepository;
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
    public UnidadResponse obtener(Long id) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new UnidadNoEncontradaException(id));
        return aRespuesta(unidad);
    }//El service devuelve DTOs, no entidades. Así la API no queda atada a la tabla.

    @Override
    @Transactional
    public UnidadResponse crear(UnidadRequest request) {
        Unidad unidad = new Unidad();
        unidad.setTitulo(request.titulo());
        unidad.setOrden(request.orden());
        return aRespuesta(unidadRepository.save(unidad));
    }

    private UnidadResponse aRespuesta(Unidad unidad) {
        return new UnidadResponse(unidad.getId(), unidad.getTitulo(),
                unidad.getDescripcion(), unidad.getOrden());
    }
}
