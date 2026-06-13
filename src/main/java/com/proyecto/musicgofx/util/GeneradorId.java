package com.proyecto.musicgofx.util;

import java.util.UUID;

public class GeneradorId {
    /**
     * Genera un ID corto único con un prefijo descriptivo.
     * Ejemplo: GeneradorId.generarId("PLY") -> "PLY-A1B2C3D4"
     */
    public static String generarId(String prefijo) {
        String uuidCorto = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return prefijo + "-" + uuidCorto;
    }
}