package com.proyecto.musicgofx.excepciones;

public class PlaylistNoEncontradaException extends RuntimeException {
    public PlaylistNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}
