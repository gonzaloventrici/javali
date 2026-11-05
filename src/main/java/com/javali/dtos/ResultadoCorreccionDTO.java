// Lo que se le devuelve: si fue correcta, la explicación y la pista
package com.javali.dtos;

public record ResultadoCorreccionDTO(boolean correcta, String explicacion, String pista) { }
