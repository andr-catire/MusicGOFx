package com.proyecto.musicgofx.modelo.entidades;

import java.util.ArrayList;
import java.util.List;

/**
 * Producto especial tipo paquete "Top Ten": un combo de canciones
 * destacadas que el usuario puede comprar como una unidad. Internamente
 * agrupa los IDs de las canciones que forman parte del paquete.
 *
 * <p>Relacion con {@link Cancion}: aqui usamos agregacion por id en
 * vez de referencia directa a objetos para mantener el paquete
 * desacoplado del catalogo a la hora de serializar a JSON.</p>
 *
 * @author Equipo MusicGO
 */
public class PaqueteTopTen extends Producto {

    private String tematica;          // ej: "Pop 2025", "Rock Clasico"
    private List<String> idsCanciones;

    public PaqueteTopTen(String id, String nombre, double precio,
                         String descripcion, String tematica) {
        super(id, nombre, precio, descripcion);
        this.tematica = tematica;
        this.idsCanciones = new ArrayList<>();
    }

    public String getTematica() {
        return tematica;
    }

    public void setTematica(String tematica) {
        this.tematica = tematica;
    }

    public List<String> getIdsCanciones() {
        return idsCanciones;
    }

    /**
     * Agrega un id de cancion al paquete. Evita duplicados.
     * @param idCancion id de la cancion a incluir
     */
    public void agregarCancion(String idCancion) {
        if (idCancion == null || idCancion.isBlank()) return;
        if (!idsCanciones.contains(idCancion)) {
            idsCanciones.add(idCancion);
        }
    }

    public int cantidadCanciones() {
        return idsCanciones.size();
    }

    @Override
    public String getTipo() {
        return "paquete_top_ten";
    }

    @Override
    public void mostrarDetalle() {
        System.out.println("  > Paquete TOP TEN");
        System.out.println("    Nombre   : " + getNombre());
        System.out.println("    Tematica : " + tematica);
        System.out.println("    Canciones incluidas: " + idsCanciones.size());
        for (String id : idsCanciones) {
            System.out.println("      - " + id);
        }
        System.out.println("    Precio   : $" + getPrecio());
    }

    @Override
    public String toString() {
        return "[TopTen] " + getId() + " - " + getNombre()
                + " (" + tematica + ") " + idsCanciones.size() + " canciones $" + getPrecio();
    }
}
