package com.proyecto.musicgofx.modelo.entidades;

import java.util.ArrayList;
import java.util.List;

/**
 * Biblioteca personal del usuario. Agrupa todas sus playlists.
 *
 * <p>Relacion con {@link Usuario}: composicion. La biblioteca solo
 * tiene sentido dentro de un usuario; si se elimina al usuario, su
 * biblioteca tambien desaparece.</p>
 *
 * @author Equipo MusicGO
 */
public class Biblioteca {

    private List<Playlist> playlists;

    public Biblioteca() {
        this.playlists = new ArrayList<>();
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public int cantidadPlaylists() {
        return playlists.size();
    }

    /**
     * Cuenta el total de canciones presentes en todas las playlists.
     * @return total de audios contados
     */
    public int totalCanciones() {
        int total = 0;
        for (Playlist p : playlists) {
            for (Audio a : p.getContenido()) {
                if (a instanceof Cancion) {
                    total++;
                }
            }
        }
        return total;
    }

    /**
     * Agrega una playlist nueva. No permite IDs duplicados.
     */
    public boolean agregarPlaylist(Playlist p) {
        if (p == null) return false;
        if (buscarPorId(p.getId()) != null) {
            return false;
        }
        playlists.add(p);
        return true;
    }

    /**
     * Busca una playlist por su ID.
     * Mejora: Se usa trim() e ignoreCase para evitar errores de entrada del usuario.
     * @param 'id Identificador alfanumérico (ej: 41601ca9)
     * @return la playlist si existe, null si no
     */
    public Playlist buscarPorId(String idBuscado) {
        if (idBuscado == null || idBuscado.isBlank()) return null;

        for (Playlist p : playlists) {
            if (p.getId().trim().equalsIgnoreCase(idBuscado.trim())) {
                return p;
            }
        }
        return null;
    }

    /**
     * Busca una playlist por su nombre exacto.
     * @return la playlist si existe, null si no
     */
    public Playlist buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) return null;

        String nombreLimpio = nombre.trim();
        for (Playlist p : playlists) {
            if (p.getNombre().equalsIgnoreCase(nombreLimpio)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Elimina una playlist de la biblioteca según su ID.
     */
    public boolean eliminarPlaylist(String id) {
        if (id == null) return false;

        String idLimpio = id.trim();
        return playlists.removeIf(p -> p.getId().equalsIgnoreCase(idLimpio));
    }
}