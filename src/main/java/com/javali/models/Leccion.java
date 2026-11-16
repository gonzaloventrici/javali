//Representa una lección corta dentro de una unidad. 
//Tiene título, la explicación del concepto (con código y texto simple) 
//y su orden dentro de la unidad.
package com.javali.models;

import jakarta.persistence.*;

@Entity
@Table(name = "lecciones") //crea tabla lecciones

public class Leccion {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne (optional = false) //ManyToOne: muchas lecciones pertenecen a una unidad
    @JoinColumn (name = "unidad_id")//JoinColumn: le pone nombre a esa columna de la clave foránea
    private Unidad unidad; //indica a qué unidad pertenece . Vinculo con clase Unidad

    @Column(nullable = false)
    private String titulo;

    @Column(length = 4000)
    private String explicacion;

    @Column(nullable = false)
    private int orden;

    public Leccion() {} //constructor vacio

    public Leccion(Long id, Unidad unidad, String titulo, String explicacion, int orden) {
        this.id = id;
        this.unidad = unidad;
        this.titulo = titulo;
        this.explicacion = explicacion;
        this.orden = orden;
    } //constructor con parametros

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Unidad getUnidadId() { return unidad; }
    public void setUnidadId(Unidad unidad) { this.unidad = unidad; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getExplicacion() { return explicacion; }
    public void setExplicacion(String explicacion) { this.explicacion = explicacion; }
    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }
} //getters y setters