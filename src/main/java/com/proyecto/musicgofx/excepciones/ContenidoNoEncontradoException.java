package com.proyecto.musicgofx.excepciones;

/**
 * Se lanza cuando se intenta acceder a un contenido (cancion, episodio,
 * producto) que no existe en el catalogo.
 *
 * @author Equipo MusicGO
 */
public class ContenidoNoEncontradoException extends Exception {

    public ContenidoNoEncontradoException(String id) {
        super("No se encontro contenido con el id '" + id + "' en el catalogo.");
    }
}
