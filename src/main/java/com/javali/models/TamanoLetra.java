package com.javali.models;

public enum TamanoLetra {
    NORMAL("Normal"),
    GRANDE("Grande"),
    MUY_GRANDE("Muy grande");

    private final String etiqueta;

    TamanoLetra(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
// Tamaños de letra que el estudiante puede elegir.
