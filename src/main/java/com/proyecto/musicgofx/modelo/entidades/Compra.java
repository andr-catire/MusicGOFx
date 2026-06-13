package com.proyecto.musicgofx.modelo.entidades;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa una transaccion de compra de un {@link Producto} por
 * parte de un usuario.
 *
 * <p>Se guarda solo el id del producto y el alias del usuario porque
 * la informacion completa vive en el catalogo y en la lista de usuarios
 * respectivamente; aqui se evita duplicar datos (DRY).</p>
 *
 * @author Equipo MusicGO
 */
public class Compra {

    private String idProducto;
    private String aliasUsuario;
    private double montoPagado;
    private LocalDateTime fecha;

    public Compra(String idProducto, String aliasUsuario, double montoPagado) {
        this.idProducto = idProducto;
        this.aliasUsuario = aliasUsuario;
        this.montoPagado = montoPagado;
        this.fecha = LocalDateTime.now();
    }

    /** Constructor adicional para reconstruir desde JSON con fecha conocida. */
    public Compra(String idProducto, String aliasUsuario, double montoPagado,
                  LocalDateTime fecha) {
        this.idProducto = idProducto;
        this.aliasUsuario = aliasUsuario;
        this.montoPagado = montoPagado;
        this.fecha = fecha;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public String getAliasUsuario() {
        return aliasUsuario;
    }

    public double getMontoPagado() {
        return montoPagado;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String fechaFormateada() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fecha.format(fmt);
    }

    @Override
    public String toString() {
        return "Compra{producto=" + idProducto
                + ", usuario=" + aliasUsuario
                + ", monto=$" + montoPagado
                + ", fecha=" + fechaFormateada() + "}";
    }
}
