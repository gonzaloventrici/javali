package com.javali.models;

public enum NivelContraste {
    NORMAL("Normal"),
    ALTO("Alto contraste");

    private final String etiqueta;

    NivelContraste(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
// Niveles de contraste de color disponibles.
