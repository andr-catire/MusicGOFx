package com.proyecto.musicgofx.modelo.entidades;

import com.proyecto.musicgofx.interfaces.Identificable;
import com.proyecto.musicgofx.util.GeneradorId;
import java.util.ArrayList;
import java.util.List;

/**
 * Lista de reproduccion. Agrupa varios {@link Audio} bajo un nombre dado.
 */
public class Playlist implements Identificable {

    private String id;
    private String nombre;
    private String aliasPropietario;
    private List<Audio> contenido;

    /**
     * Constructor para crear una nueva playlist con ID automatico corto.
     */
    public Playlist(String nombre, String aliasPropietario) {
        this.id = GeneradorId.generarId("PLY"); // Generación del ID corto con prefijo
        this.nombre = nombre;
        this.aliasPropietario = aliasPropietario;
        this.contenido = new ArrayList<>();
    }

    /**
     * Constructor para reconstruir desde JSON.
     */
    public Playlist(String id, String nombre, String aliasPropietario, List<Audio> contenido) {
        this.id = id;
        this.nombre = nombre;
        this.aliasPropietario = aliasPropietario;
        this.contenido = (contenido != null) ? contenido : new ArrayList<>();
    }

    @Override
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getAliasPropietario() {
        return aliasPropietario;
    }

    public List<Audio> getContenido() {
        return contenido;
    }

    /**
     * Agrega un audio a la playlist.
     */
    public boolean agregarAudio(Audio audio) {
        if (audio == null) return false;
        for (Audio a : contenido) {
            if (a.getId().equals(audio.getId())) return false;
        }
        return contenido.add(audio);
    }

    /**
     * Quita el audio de la playlist.
     */
    public boolean removerAudio(String idAudio) {
        if (idAudio == null) return false;
        return contenido.removeIf(a -> a.getId().equals(idAudio));
    }
    public int cantidadAudios(){
        return contenido.size();
    }
    @Override
    public String toString() {
        return "[" + id + "] Playlist: '" + nombre + "' (" + contenido.size() + " audios)";
    }
}