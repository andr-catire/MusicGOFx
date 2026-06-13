package com.proyecto.musicgofx.modelo.servicios;

import com.proyecto.musicgofx.modelo.entidades.Estadisticas;
import com.proyecto.musicgofx.modelo.entidades.Usuario;

/**
 * Servicio dedicado a mostrar/exportar estadisticas de un usuario.
 *
 * <p>Las estadisticas las acumula el modelo a medida que pasan los
 * eventos (reproducir, comprar, etc.); este gestor solo se encarga
 * de presentarlas bonito y dejar el codigo de la UI mas limpio.</p>
 *
 * @author Equipo MusicGO
 */
public class GestorEstadisticas {

    private final GestorUsuarios gestorUsuarios;

    public GestorEstadisticas(GestorUsuarios gestorUsuarios) {
        this.gestorUsuarios = gestorUsuarios;
    }

    /**
     * Muestra por consola un reporte visual detallado del perfil del usuario.
     * @param idUsuarioOAlias ID o alias del usuario a consultar.
     */
    public void mostrarReporteCompleto(String idUsuarioOAlias) {
        Usuario usuario = gestorUsuarios.buscarPorIdOAlias(idUsuarioOAlias);

        if (usuario == null) {
            System.err.println("Error: No se pueden generar estadisticas. Usuario no encontrado.");
            return;
        }

        Estadisticas stats = usuario.getEstadisticas();

        System.out.println("\n===========================================");
        System.out.println("   REPORTE DE ACTIVIDAD: " + usuario.getNombre().toUpperCase());
        System.out.println("===========================================");
        System.out.println(" > Tiempo total escuchado:  " + stats.tiempoFormateado());
        System.out.println(" > Reproducciones totales:  " + stats.getReproduccionesTotales());
        System.out.println(" > Canciones en biblioteca: " + stats.getCancionesEnBiblioteca());
        System.out.println(" > Compras realizadas:      " + stats.getComprasRealizadas());
        System.out.println("-------------------------------------------");

        if (stats.getReproduccionesTotales() > 100) {
            System.out.println(" ¡Eres un oyente nivel Leyenda! ");
        } else if (stats.getReproduccionesTotales() > 0) {
            System.out.println(" ¡Sigue descubriendo nueva musica! ");
        } else {
            System.out.println(" Tu historial esta vacio. ¡Empieza a escuchar ahora! ");
        }
        System.out.println("===========================================\n");
    }

    /**
     * Simula la exportacion de estadisticas a un formato de texto plano.
     * (En una fase futura, esto podria escribir en un archivo .txt o .csv)
     */
    public void exportarEstadisticas(String idUsuarioOAlias) {
        Usuario usuario = gestorUsuarios.buscarPorIdOAlias(idUsuarioOAlias);
        if (usuario != null) {
            System.out.println("[Sistema] Exportando estadisticas de " + usuario.getNombre() + "...");
            System.out.println("[Exito] Los datos se han enviado al buffer de impresion.");
        } else {
            System.err.println("Error: Usuario no encontrado para exportar.");
        }
    }
}