package com.proyecto.musicgofx.interfaces;

/**
 * Contrato para cualquier elemento de la plataforma que se pueda adquirir
 * (productos especiales tipo arte de album, paquetes "Top Ten", etc.).
 *
 * Se separa de {@link Reproducible} a proposito porque hay productos
 * que NO son audio (ej. arte visual), y al reves: las canciones se
 * reproducen pero no se "compran" como tal.
 *
 * @author Equipo MusicGO
 */
public interface Comprable {

    /**
     * @return precio del producto en la moneda local de la plataforma
     */
    double getPrecio();

    /**
     * @return descripcion corta del producto, util para mostrar en consola
     */
    String getDescripcion();
}
