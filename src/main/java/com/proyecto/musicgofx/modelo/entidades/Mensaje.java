package com.proyecto.musicgofx.modelo.entidades;

import java.time.LocalDateTime;

/**
 * Mensaje interno que viaja entre el usuario y la plataforma.
 *
 * Ejemplos de uso en la fase 1:
 * <ul>
 *   <li>El usuario pide reproducir un contenido (REPRODUCIR).</li>
 *   <li>El usuario solicita comprar un producto (COMPRAR).</li>
 *   <li>La plataforma confirma una accion al usuario (CONFIRMACION).</li>
 *   <li>La plataforma reporta un error (ERROR).</li>
 * </ul>
 *
 * <p>La idea es modelar el requerimiento de "interaccion y mensajeria"
 * sin acoplar las clases al canal concreto por el que se manda el
 * mensaje (esto en fase 2 podria ser email, push, etc.).</p>
 *
 * @author Equipo MusicGO
 */
public class Mensaje {

    /** Tipos posibles de mensaje dentro del sistema. */
    public enum Tipo {
        REPRODUCIR, COMPRAR, CONFIRMACION, ERROR, INFO
    }

    private String emisor;
    private String receptor;
    private Tipo tipo;
    private String contenido;
    private LocalDateTime fecha;

    public Mensaje(String emisor, String receptor, Tipo tipo, String contenido) {
        this.emisor = emisor;
        this.receptor = receptor;
        this.tipo = tipo;
        this.contenido = contenido;
        this.fecha = LocalDateTime.now();
    }

    public String getEmisor() {
        return emisor;
    }

    public String getReceptor() {
        return receptor;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public String getContenido() {
        return contenido;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    @Override
    public String toString() {
        return "[" + tipo + "] " + emisor + " -> " + receptor + ": " + contenido;
    }
}
