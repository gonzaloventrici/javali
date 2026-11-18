//Representa la devolución específica de una opción incorrecta.
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

@Entity 
@Table (name = "retroalimentacion_opcion")
public class RetroalimentacionOpcion {
    //variables
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(optional = false) 
    @JoinColumn(name="ejercicio_id")
    private Ejercicio ejercicio;

    @Column(nullable = false)
    private Integer indiceOpcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaError categoriaError;

    @Column(nullable = false, length = 1000)
    private String explicacion;

    @Column(nullable = false, length = 500)
    private String pista;

    public RetroalimentacionOpcion() {}
    // Constructor vacío requerido por Hibernate.

    //geters & setters
    public Long getId() { return id; }
    public void setId(Long id) {this.id = id;}
    public Ejercicio getEjercicio() {return ejercicio;}
    public void setEjercicio(Ejercicio ejercicio) {this.ejercicio = ejercicio;}
    public Integer getIndiceOpcion() {return indiceOpcion;}
    public void setIndiceOpcion(Integer indiceOpcion) {this.indiceOpcion = indiceOpcion;}
    public CategoriaError getCategoriaError() {return categoriaError;}
    public void setCategoriaError(CategoriaError categoriaError) {this.categoriaError = categoriaError;}
    public String getExplicacion() {return explicacion;}
    public void setExplicacion(String explicacion) {this.explicacion = explicacion;}
    public String getPista() {return pista;}
    public void setPista(String pista) {this.pista = pista;}
}

// Guarda la categoría, explicación y pista de una opción incorrecta.

