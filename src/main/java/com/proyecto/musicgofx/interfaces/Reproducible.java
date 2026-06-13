package com.proyecto.musicgofx.interfaces;

/**
 * Contrato que cumple cualquier contenido reproducible dentro de MusicGO.
 *
 * En la fase 1 lo implementan {@code Cancion} y {@code EpisodioPodcast},
 * pero la idea es que en fase 2 puedan agregarse otros tipos de audio
 * sin tener que tocar el motor de reproduccion.
 *
 * @author Equipo MusicGO
 */
public interface Reproducible {

    /**
     * Simula la reproduccion del contenido por consola.
     * Cada implementacion decide que muestra (titulo, duracion, creditos...).
     */
    void reproducir();

    /**
     * @return duracion total en segundos
     */
    int getDuracionSegundos();

    /**
     * @return titulo legible del contenido
     */
    String getTitulo();
}
