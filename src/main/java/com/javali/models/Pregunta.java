package com.javali.models;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "preguntas")
public class Pregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "evaluacion_id", nullable = false)
    private Evaluacion evaluacion;

    @Column(nullable = false, length = 1000)
    private String enunciado;

    @Column(length = 4000)
    private String codigo;

    @Column(nullable = false)
    private int orden;

    @ElementCollection
    @CollectionTable(
            name = "pregunta_opciones",
            joinColumns = @JoinColumn(name = "pregunta_id")
    )
    @OrderColumn(name = "posicion")
    @Column(name = "texto", nullable = false, length = 500)
    private List<String> opciones = new ArrayList<>();

    @Column(nullable = false)
    private Integer indiceCorrecto;

    public Pregunta() {} // Constructor vacío requerido por Hibernate.

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public Evaluacion getEvaluacion() {return evaluacion;}
    public void setEvaluacion(Evaluacion evaluacion) {this.evaluacion = evaluacion;}
    public String getEnunciado() {return enunciado;}
    public void setEnunciado(String enunciado) {this.enunciado = enunciado;}
    public String getCodigo() {return codigo;}
    public void setCodigo(String codigo) {this.codigo = codigo;}
    public int getOrden() {return orden;}
    public void setOrden(int orden) {this.orden = orden;}
    public List<String> getOpciones() {return opciones;}
    public void setOpciones(List<String> opciones) {this.opciones = opciones;}
    public Integer getIndiceCorrecto() {return indiceCorrecto;}
    public void setIndiceCorrecto(Integer indiceCorrecto) {this.indiceCorrecto = indiceCorrecto;}
}
// Representa una pregunta DE OPCIONES dentro de una evaluación.