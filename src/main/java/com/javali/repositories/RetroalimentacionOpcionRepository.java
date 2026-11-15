//Busca la retroalimentación correspondiente a una opción específica
//Devuelve un Optional porque puede existir o no una retroalimentación 
//cargada para esa opción.
package com.javali.repositories;

import com.javali.models.RetroalimentacionOpcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RetroalimentacionOpcionRepository
        extends JpaRepository<RetroalimentacionOpcion, Long> {
    Optional<RetroalimentacionOpcion>
            findByEjercicio_IdAndIndiceOpcion(
                    Long ejercicioId,
                    Integer indiceOpcion
            );
}
// Busca una retroalimentación por ejercicio e índice de opción.