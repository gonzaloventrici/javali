//Representa una lección corta dentro de una unidad. 
//Tiene título, la explicación del concepto (con código y texto simple) 
//y su orden dentro de la unidad.
package com.javali.models;

public class Leccion {
    private Long id;
    private Long unidadId; //indica a qué unidad pertenece . Vinculo con clase Unidad
    private String titulo;
    private String explicacion;
    private int orden;

    public Leccion() {} //constructor vacio

    public Leccion(Long id, Long unidadId, String titulo, String explicacion, int orden) {
        this.id = id;
        this.unidadId = unidadId;
        this.titulo = titulo;
        this.explicacion = explicacion;
        this.orden = orden;
    } //constructor con parametros

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUnidadId() { return unidadId; }
    public void setUnidadId(Long unidadId) { this.unidadId = unidadId; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getExplicacion() { return explicacion; }
    public void setExplicacion(String explicacion) { this.explicacion = explicacion; }
    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }
} //getters y setters