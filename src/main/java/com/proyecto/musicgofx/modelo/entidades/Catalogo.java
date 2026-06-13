package com.proyecto.musicgofx.modelo.entidades;

import java.util.ArrayList;
import java.util.List;

/**
 * Catalogo global de la plataforma. Agrupa todos los {@link Audio}
 * disponibles (canciones y episodios) y todos los {@link Producto}
 * que se pueden comprar.
 *
 * <p>Es esencialmente un contenedor con buscadores; toda la logica
 * de carga/persistencia se hace en {@code GestorCatalogo}.</p>
 *
 * @author Equipo MusicGO
 */
public class Catalogo {

    private List<Audio> audios;
    private List<Producto> productos;

    public Catalogo() {
        this.audios = new ArrayList<>();
        this.productos = new ArrayList<>();
    }

    public List<Audio> getAudios() {
        return audios;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void agregarAudio(Audio a) {
        if (a == null) return;

        for (Audio existente : audios) {
            if (existente.getId().equals(a.getId())) return;
        }
        audios.add(a);
    }

    public void agregarProducto(Producto p) {
        if (p == null) return;
        for (Producto existente : productos) {
            if (existente.getId().equals(p.getId())) return;
        }
        productos.add(p);
    }

    /**
     * @param id id buscado
     * @return el audio con ese id, o null si no existe
     */
    public Audio buscarAudio(String id) {
        if (id == null) return null;
        for (Audio a : audios) {
            if (a.getId().equals(id)) return a;
        }
        return null;
    }

    /**
     * @param id id buscado
     * @return el producto con ese id, o null si no existe
     */
    public Producto buscarProducto(String id) {
        if (id == null) return null;
        for (Producto p : productos) {
            if (p.getId().equals(id)) return p;
        }
        return null;
    }

    /**
     * Filtra el catalogo y devuelve solo las canciones.
     */
    public List<Cancion> soloCanciones() {
        List<Cancion> res = new ArrayList<>();
        for (Audio a : audios) {
            if (a instanceof Cancion) res.add((Cancion) a);
        }
        return res;
    }

    /**
     * Filtra el catalogo y devuelve solo los episodios de podcast.
     */
    public List<EpisodioPodcast> soloPodcasts() {
        List<EpisodioPodcast> res = new ArrayList<>();
        for (Audio a : audios) {
            if (a instanceof EpisodioPodcast) res.add((EpisodioPodcast) a);
        }
        return res;
    }
}
