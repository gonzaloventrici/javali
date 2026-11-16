package com.javali.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cursos")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    @Column(nullable = false)
    private boolean activo;

    public Curso() {} // Constructor vacío requerido por Hibernate.

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public String getTitulo() {return titulo;}
    public void setTitulo(String titulo) {this.titulo = titulo;}
    public String getDescripcion() {return descripcion;}
    public void setDescripcion(String descripcion) {this.descripcion = descripcion;}
    public boolean isActivo() {return activo;}
    public void setActivo(boolean activo) {this.activo = activo;}
}
// Representa un curso que agrupa diferentes unidades.