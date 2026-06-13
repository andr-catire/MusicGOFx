package com.proyecto.musicgofx.modelo.entidades;
import java.util.UUID;
/**
 * Producto especial: arte visual de un album. Es una imagen o
 * coleccion de imagenes asociadas a un album musical y que el usuario
 * puede adquirir como contenido digital.
 *
 * @author Equipo MusicGO
 */
public class ArteVisualAlbum extends Producto {

    private String albumAsociado;
    private String artista;
    private String formato;   // "PNG", "PSD", "wallpaper 4K", etc.

    public ArteVisualAlbum(String id, String nombre, double precio, String descripcion,
                           String albumAsociado, String artista, String formato) {
        super(id, nombre, precio, descripcion);
        this.albumAsociado = albumAsociado;
        this.artista = artista;
        this.formato = formato;
    }

    public String getAlbumAsociado() {
        return albumAsociado;
    }

    public String getArtista() {
        return artista;
    }

    public String getFormato() {
        return formato;
    }

    public void setAlbumAsociado(String albumAsociado) {
        this.albumAsociado = albumAsociado;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    @Override
    public String getTipo() {
        return "arte_visual";
    }

    @Override
    public void mostrarDetalle() {
        System.out.println("  > Arte Visual de Album");
        System.out.println("    Nombre  : " + getNombre());
        System.out.println("    Album   : " + albumAsociado + " - " + artista);
        System.out.println("    Formato : " + formato);
        System.out.println("    Precio  : $" + getPrecio());
    }

    @Override
    public String toString() {
        return "[Arte] " + getId() + " - " + getNombre()
                + " (" + albumAsociado + ") $" + getPrecio();
    }
}
