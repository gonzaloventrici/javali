package com.javali.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "preferencias_usuario",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_preferencias_usuario",
                columnNames = "usuario_id"
        )
)
public class PreferenciasUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TamanoLetra tamanoLetra = TamanoLetra.NORMAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelContraste contraste = NivelContraste.NORMAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tipografia tipografia = Tipografia.SISTEMA;

    @Column(nullable = false)
    private boolean mostrarRacha = true;

    public PreferenciasUsuario() {} // Constructor vacío requerido por Hibernate.

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public TamanoLetra getTamanoLetra() { return tamanoLetra; }
    public void setTamanoLetra(TamanoLetra tamanoLetra) { this.tamanoLetra = tamanoLetra; }
    public NivelContraste getContraste() { return contraste; }
    public void setContraste(NivelContraste contraste) { this.contraste = contraste; }
    public Tipografia getTipografia() { return tipografia; }
    public void setTipografia(Tipografia tipografia) { this.tipografia = tipografia; }
    public boolean isMostrarRacha() { return mostrarRacha; }
    public void setMostrarRacha(boolean mostrarRacha) { this.mostrarRacha = mostrarRacha; }
}
// Guarda los ajustes de accesibilidad y la racha opcional de cada estudiante.
