package com.javali.models;

public enum Tipografia {
    SISTEMA("Predeterminada"),
    LEGIBLE("Legible (más ancha)"),
    DISLEXIA("Amigable con la dislexia");

    private final String etiqueta;

    Tipografia(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
// Tipografías disponibles; se resuelven con fuentes del sistema.
