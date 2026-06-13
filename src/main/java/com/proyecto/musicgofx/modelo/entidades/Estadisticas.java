package com.proyecto.musicgofx.modelo.entidades;

/**
 * Acumula las estadisticas de un usuario dentro de la plataforma.
 * Vive en composicion con {@link Usuario}: si se elimina al usuario
 * tambien se va su objeto de estadisticas (no tiene sentido por
 * separado).
 *
 * @author Equipo MusicGO
 */
public class Estadisticas {

    private long tiempoEscuchaSegundos;
    private int cancionesEnBiblioteca;
    private int comprasRealizadas;
    private int reproduccionesTotales;

    public Estadisticas() {
        this.tiempoEscuchaSegundos = 0;
        this.cancionesEnBiblioteca = 0;
        this.comprasRealizadas = 0;
        this.reproduccionesTotales = 0;
    }

    public Estadisticas(long tiempoEscuchaSegundos, int cancionesEnBiblioteca,
                        int comprasRealizadas, int reproduccionesTotales) {
        this.tiempoEscuchaSegundos = tiempoEscuchaSegundos;
        this.cancionesEnBiblioteca = cancionesEnBiblioteca;
        this.comprasRealizadas = comprasRealizadas;
        this.reproduccionesTotales = reproduccionesTotales;
    }

    public long getTiempoEscuchaSegundos() {
        return tiempoEscuchaSegundos;
    }

    public int getCancionesEnBiblioteca() {
        return cancionesEnBiblioteca;
    }

    public int getComprasRealizadas() {
        return comprasRealizadas;
    }

    public int getReproduccionesTotales() {
        return reproduccionesTotales;
    }

    public void sumarTiempoEscucha(int segundos) {
        if (segundos <= 0) return;
        this.tiempoEscuchaSegundos += segundos;
        this.reproduccionesTotales++;
    }

    public void setCancionesEnBiblioteca(int cantidad) {
        if (cantidad < 0) return;
        this.cancionesEnBiblioteca = cantidad;
    }

    public void registrarCompra() {
        this.comprasRealizadas++;
    }

    /**
     * Devuelve el tiempo total de escucha en formato hh:mm:ss.
     * @return tiempo formateado
     */
    public String tiempoFormateado() {
        long h = tiempoEscuchaSegundos / 3600;
        long m = (tiempoEscuchaSegundos % 3600) / 60;
        long s = tiempoEscuchaSegundos % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }
}
