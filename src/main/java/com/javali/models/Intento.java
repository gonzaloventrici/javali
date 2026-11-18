package com.javali.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "intentos")
public class Intento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ejercicio_id")
    private Ejercicio ejercicio;

    @Column(nullable = false)
    private Integer indiceSeleccionado;

    @Column(nullable = false)
    private boolean correcta;

    @Enumerated(EnumType.STRING)
    private CategoriaError categoriaError;

    @Column(nullable = false)
    private LocalDateTime fecha;

    public Intento() {} // Constructor vacío requerido por Hibernate.
    
    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public Usuario getUsuario() {return usuario;}
    public void setUsuario(Usuario usuario) {this.usuario = usuario;}
    public Ejercicio getEjercicio() {return ejercicio;}
    public void setEjercicio(Ejercicio ejercicio) {this.ejercicio = ejercicio;}
    public Integer getIndiceSeleccionado() {return indiceSeleccionado;}
    public void setIndiceSeleccionado(Integer indiceSeleccionado) {this.indiceSeleccionado = indiceSeleccionado;}
    public boolean isCorrecta() {return correcta;}
    public void setCorrecta(boolean correcta) {this.correcta = correcta;}
    public CategoriaError getCategoriaError() {return categoriaError;}
    public void setCategoriaError(CategoriaError categoriaError) {this.categoriaError = categoriaError;}
    public LocalDateTime getFecha() {return fecha;}
    public void setFecha(LocalDateTime fecha) {this.fecha = fecha;}
}
// Registra la respuesta de un usuario a un ejercicio.