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
@Table(name = "evaluaciones")
public class Evaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private int respuestasCorrectas;

    @Column(nullable = false)
    private int totalPreguntas;

    @Column(nullable = false)
    private double porcentaje;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelCurso nivelRecomendado;

    @Enumerated(EnumType.STRING)
    private NivelCurso nivelElegido;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private boolean finalizada; // Indica si el estudiante ya respondió la evaluación.

    public Evaluacion() {} // Constructor vacío requerido por Hibernate.

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public Usuario getUsuario() {return usuario;}
    public void setUsuario(Usuario usuario) {this.usuario = usuario;}
    public int getRespuestasCorrectas() {return respuestasCorrectas;}
    public void setRespuestasCorrectas(int respuestasCorrectas) {this.respuestasCorrectas = respuestasCorrectas;}
    public int getTotalPreguntas() {return totalPreguntas;}
    public void setTotalPreguntas(int totalPreguntas) {this.totalPreguntas = totalPreguntas;}
    public double getPorcentaje() {return porcentaje;}
    public void setPorcentaje(double porcentaje) {this.porcentaje = porcentaje;}
    public NivelCurso getNivelRecomendado() {return nivelRecomendado;}
    public void setNivelRecomendado(NivelCurso nivelRecomendado) {this.nivelRecomendado = nivelRecomendado;}
    public NivelCurso getNivelElegido() {return nivelElegido;}
    public void setNivelElegido(NivelCurso nivelElegido) {this.nivelElegido = nivelElegido;}
    public LocalDateTime getFecha() {return fecha;}
    public void setFecha(LocalDateTime fecha) {this.fecha = fecha;}
    public boolean isFinalizada() {return finalizada;}
    public void setFinalizada(boolean finalizada) {this.finalizada = finalizada;}
}
// Guarda el resultado y los niveles recomendado y elegido.