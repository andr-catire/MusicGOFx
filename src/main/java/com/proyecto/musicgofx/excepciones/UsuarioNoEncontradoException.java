package com.proyecto.musicgofx.excepciones;

/**
 * Se lanza al intentar buscar/eliminar/modificar un usuario que no esta
 * registrado en el sistema.
 *
 * @author Equipo MusicGO
 */
public class UsuarioNoEncontradoException extends Exception {

    public UsuarioNoEncontradoException(String alias) {
        super("No se encontro ningun usuario con el alias.");
    }
}
