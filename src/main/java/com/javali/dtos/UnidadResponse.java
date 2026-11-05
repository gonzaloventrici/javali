package com.javali.dtos;

public record UnidadResponse(Long id, String titulo, String descripcion, int orden) {}

//DTO utilizado para devolver información de una unidad al cliente.
//Contiene los datos que se envían en las respuestas de la API: id, titulo, descripcion y orden.
//Se usa para no exponer directamente la entidad Unidad.
//Representa la salida de operaciones como listar, obtener o crear unidades.