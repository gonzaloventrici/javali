//Representa una actividad dentro de una lección
package com.javali.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ejercicios")
public class Ejercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "leccion_id")
    private Leccion leccion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEjercicio tipo;

    @Column(nullable = false, length = 1000)
    private String enunciado;

    @Column(length = 4000)
    private String codigo;

    @Column(length = 1000)
    private String explicacion;

    @Column(length = 500)
    private String pista;

    @Column(nullable = false)
    private int orden;

    @ElementCollection
    @CollectionTable(name = "ejercicio_opciones", joinColumns = @JoinColumn(name = "ejercicio_id"))
    @OrderColumn(name = "posicion")
    @Column(name = "texto", nullable = false, length = 500)
    private List<String> opciones = new ArrayList<>();

    private Integer indiceCorrecto;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Leccion getLeccion() { return leccion; }
    public void setLeccion(Leccion leccion) { this.leccion = leccion; }
    public TipoEjercicio getTipo() { return tipo; }
    public void setTipo(TipoEjercicio tipo) { this.tipo = tipo; }
    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getExplicacion() { return explicacion; }
    public void setExplicacion(String explicacion) { this.explicacion = explicacion; }
    public String getPista() { return pista; }
    public void setPista(String pista) { this.pista = pista; }
    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }
    public List<String> getOpciones() { return opciones; }
    public void setOpciones(List<String> opciones) { this.opciones = opciones; }
    public Integer getIndiceCorrecto() { return indiceCorrecto; }
    public void setIndiceCorrecto(Integer indiceCorrecto) { this.indiceCorrecto = indiceCorrecto; }
}