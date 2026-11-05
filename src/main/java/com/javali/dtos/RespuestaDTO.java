// Lo que envía el estudiante: un indice (opciones o error) o una lista orden (ordenar)
package com.javali.dtos;

import java.util.List;

public record RespuestaDTO(Integer indice, List<Integer> orden) {}
