package com.proyecto.musicgofx.excepciones;

public class PagoRechazadoException extends RuntimeException {
    public PagoRechazadoException(String mensaje) {
        super(mensaje);
    }
}
