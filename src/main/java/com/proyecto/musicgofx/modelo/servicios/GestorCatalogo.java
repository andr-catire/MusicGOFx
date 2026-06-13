package com.proyecto.musicgofx.modelo.servicios;

import com.proyecto.musicgofx.modelo.entidades.Audio;
import com.proyecto.musicgofx.modelo.entidades.Producto;
import com.proyecto.musicgofx.modelo.persistencia.RepositorioDatos;
import java.util.ArrayList;
import java.util.List;

/**
 * Administra el inventario completo de la aplicacion, incluyendo
 * audios del catalogo y productos disponibles para la venta.
 *
 * @author Equipo MusicGO
 */
public class GestorCatalogo {

    private final RepositorioDatos repositorio;
    private List<Audio> catalogoCompleto;
    private List<Producto> productos;

    /**
     * Inicializa el gestor y las listas de almacenamiento en memoria.
     */
    public GestorCatalogo() {
        this.repositorio = new RepositorioDatos();
        this.catalogoCompleto = new ArrayList<>();
        this.productos = new ArrayList<>();
    }

    /**
     * Carga todos los datos desde los archivos JSON (catalogo, canciones y productos)
     * y los centraliza en las listas del gestor.
     *
     * @return La cantidad total de elementos de audio cargados.
     */
    public int cargarDesdeJson() {
        // Carga de audios desde catalogo.json y canciones.json
        List<Audio> deCatalogo = repositorio.cargarCatalogo();
        List<Audio> deCanciones = repositorio.cargarAudios();

        this.catalogoCompleto.clear();
        this.catalogoCompleto.addAll(deCatalogo);
        this.catalogoCompleto.addAll(deCanciones);

        // Carga de productos desde productos.json
        this.productos = repositorio.cargarProductos();

        return this.catalogoCompleto.size();
    }

    /**
     * Obtiene la lista de todos los audios disponibles.
     *
     * @return Lista de objetos Audio.
     */
    public List<Audio> getTodosLosAudios() {
        return catalogoCompleto;
    }

    /**
     * Obtiene la lista de todos los productos disponibles en la tienda.
     *
     * @return Lista de objetos Producto.
     */
    public List<Producto> getTodosLosProductos() {
        return productos;
    }

    /**
     * Busca un audio especifico mediante su identificador unico.
     *
     * @param id Identificador del audio.
     * @return El audio encontrado o null.
     */
    public Audio buscarAudioPorId(String id) {
        for (Audio audio : catalogoCompleto) {
            if (audio.getId().equalsIgnoreCase(id)) {
                return audio;
            }
        }
        return null;
    }

    /**
     * Busca un producto especifico mediante su identificador unico.
     *
     * @param id Identificador del producto.
     * @return El producto encontrado o null.
     */

    /**
     * Busca un producto en la lista cargada comparando por ID o por nombre.
     * * @param criterio El ID o nombre buscado.
     * @return El producto encontrado o null si no existe.
     */
    public Producto buscarProductoPorIdONombre(String criterio) {
        for (Producto p : getTodosLosProductos()) {
            if (p.getId().equalsIgnoreCase(criterio) || p.getNombre().equalsIgnoreCase(criterio)) {
                return p;
            }
        }
        return null;
    }
}