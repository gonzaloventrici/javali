package com.javali.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(name = "progresos", uniqueConstraints = {
                @UniqueConstraint(name = "uk_progreso_usuario_leccion",
                        columnNames = {
                                "usuario_id",
                                "leccion_id"
                        }
                )
        }
)
public class Progreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "leccion_id")
    private Leccion leccion;

    @Column(nullable = false)
    private int cantidadIntentos;

    @Column(nullable = false)
    private int respuestasCorrectas;

    @Column(nullable = false)
    private double porcentajeAciertos;

    @Column(nullable = false)
    private boolean completada;

    @Column(nullable = false)
    private LocalDateTime ultimaActividad;

    public Progreso() {} // Constructor vacío requerido por Hibernate.

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public Usuario getUsuario() {return usuario;}
    public void setUsuario(Usuario usuario) {this.usuario = usuario;}
    public Leccion getLeccion() {return leccion;}
    public void setLeccion(Leccion leccion) {this.leccion = leccion;}
    public int getCantidadIntentos() {return cantidadIntentos;}
    public void setCantidadIntentos(int cantidadIntentos) {this.cantidadIntentos = cantidadIntentos;}
    public int getRespuestasCorrectas() {return respuestasCorrectas;}
    public void setRespuestasCorrectas(int respuestasCorrectas) {this.respuestasCorrectas = respuestasCorrectas;}
    public double getPorcentajeAciertos() {return porcentajeAciertos;}
    public void setPorcentajeAciertos(double porcentajeAciertos) {this.porcentajeAciertos = porcentajeAciertos;}
    public boolean isCompletada() {return completada;}
    public void setCompletada(boolean completada) {this.completada = completada;}
    public LocalDateTime getUltimaActividad() {return ultimaActividad;}
    public void setUltimaActividad(LocalDateTime ultimaActividad) {this.ultimaActividad = ultimaActividad;}
}
// Guarda el avance de un usuario dentro de una lección.