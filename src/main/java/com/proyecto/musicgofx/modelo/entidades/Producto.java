package com.proyecto.musicgofx.modelo.entidades;

import com.proyecto.musicgofx.interfaces.Comprable;
import com.proyecto.musicgofx.interfaces.Identificable;
import com.proyecto.musicgofx.util.GeneradorId;

/**
 * Clase abstracta que representa cualquier producto especial que MusicGO
 * pone a la venta dentro de la plataforma. No todos los productos son
 * audio: existen artes visuales y paquetes de coleccion.
 *
 * <p>Por eso se separa de {@link Audio}: comparten el hecho de tener
 * id, pero no la naturaleza ni los atributos.</p>
 *
 * @author Equipo MusicGO
 */
public abstract class Producto implements Comprable, Identificable {

    private String id;
    private String nombre;
    private double precio;
    private String descripcion;

    /**
     * Constructor para RECONSTRUIR un producto existente (ej. desde JSON).
     */
    protected Producto(String id, String nombre, double precio, String descripcion) {
        this.id = (id == null || id.isBlank()) ? GeneradorId.generarId("PRD") : id;
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
    }

    /**
     * Constructor para CREAR un producto nuevo.
     * Genera automaticamente un identificador unico corto con prefijo.
     */
    protected Producto(String nombre, double precio, String descripcion) {
        this(GeneradorId.generarId("PRD"), nombre, precio, descripcion);
    }

    @Override
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public double getPrecio() {
        return precio;
    }

    @Override
    public String getDescripcion() {
        return descripcion;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPrecio(double precio) {
        if (precio < 0) return;
        this.precio = precio;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return cadena identificadora del tipo de producto (para JSON)
     */
    public abstract String getTipo();

    /**
     * Cada subclase decide como se "muestra" cuando alguien lo compra.
     */
    public abstract void mostrarDetalle();
}