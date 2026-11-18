package com.javali.validators;

import com.javali.models.TipoEjercicio;
import com.javali.utils.OrdenUtils;
import org.springframework.stereotype.Component;

import java.util.List;

// Reglas de cada tipo de ejercicio, tanto al crearlo como al responderlo.
@Component
public class EjercicioValidator {

    // Comprueba que el ejercicio tenga los datos que exige su tipo.
    public void validarEjercicio(
            TipoEjercicio tipo,
            List<String> opciones,
            Integer indiceCorrecto,
            List<Integer> ordenCorrecto) {

        if (opciones == null || opciones.size() < 2) {
            throw new IllegalArgumentException(
                    "El ejercicio debe contener al menos dos opciones"
            );
        }
        for (String opcion : opciones) {
            if (opcion == null || opcion.isBlank()) {
                throw new IllegalArgumentException(
                        "Las opciones no pueden estar vacías"
                );
            }
        }
        if (tipo == TipoEjercicio.ORDENAR) {
            if (!OrdenUtils.esPermutacion(ordenCorrecto, opciones.size())) {
                throw new IllegalArgumentException(
                        "El orden correcto debe incluir cada opción exactamente una vez"
                );
            }
            return;
        }
        if (indiceCorrecto == null
                || indiceCorrecto < 0
                || indiceCorrecto >= opciones.size()) {
            throw new IllegalArgumentException(
                    "El índice correcto debe corresponder a una opción existente"
            );
        }
    }

    // Comprueba que llegue el dato que corresponde al tipo de ejercicio.
    public void validarRespuesta(
            TipoEjercicio tipo,
            int cantidadOpciones,
            Integer indiceSeleccionado,
            List<Integer> ordenSeleccionado) {

        if (tipo == TipoEjercicio.ORDENAR) {
            if (!OrdenUtils.esPermutacion(ordenSeleccionado, cantidadOpciones)) {
                throw new IllegalArgumentException(
                        "Debés ordenar todas las líneas, sin repetir ninguna"
                );
            }
            return;
        }
        if (indiceSeleccionado == null
                || indiceSeleccionado < 0
                || indiceSeleccionado >= cantidadOpciones) {
            throw new IllegalArgumentException(
                    "Debés elegir una de las opciones disponibles"
            );
        }
    }
}
