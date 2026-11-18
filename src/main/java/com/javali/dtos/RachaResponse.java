package com.javali.dtos;

public record RachaResponse(
        int rachaActual,
        int mejorRacha,
        boolean practicoHoy) {}
// Devuelve los días seguidos de práctica; es solo un motivador.
