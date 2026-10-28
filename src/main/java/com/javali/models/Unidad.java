//UNIDAD: Bloque temático del curso: variables, condicionales, bucles, clases y objetos. 
//Cada unidad tiene un título, una descripción y un orden para saber en qué posición aparece.
package com.javali.models; 

public class Unidad {
    private Long id;
    private String titulo;
    private String descripcion;
    private int orden;

    public Unidad() {} //constructor vacio OBLIGATORIO springboot

    public Unidad(Long id, String titulo, String descripcion, int orden) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.orden = orden;
    } //constructor con parametros

    public Long getId() { return id; } //trae id
    public void setId(Long id) { this.id = id; } //setea id
    public String getTitulo() { return titulo; } //trae el titulo
    public void setTitulo(String titulo) { this.titulo = titulo; } //setea el titulo
    public String getDescripcion() { return descripcion; } //trae la descripcion
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; } //setea la descripcion
    public int getOrden() { return orden; } //trae el orde
    public void setOrden(int orden) { this.orden = orden; } //setea el orden
}