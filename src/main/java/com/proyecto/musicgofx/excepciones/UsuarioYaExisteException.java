package com.proyecto.musicgofx.excepciones;

/**
 * Se lanza cuando se intenta registrar un usuario cuyo alias ya existe
 * dentro del sistema. La validacion de duplicados se hace en el
 * GestorUsuarios antes de persistir.
 *
 * @author Equipo MusicGO
 */
public class UsuarioYaExisteException extends Exception {

    public UsuarioYaExisteException(String mensaje) {
        super(mensaje);
    }
}
