package com.proyecto.musicgofx.modelo.persistencia;

import com.proyecto.musicgofx.modelo.entidades.*;
import java.util.List;

public class JsonWriter {

    public String crearJsonAudios(List<Audio> lista) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < lista.size(); i++) {
            Audio a = lista.get(i);
            sb.append("  {");
            sb.append("\"id\":\"").append(a.getId()).append("\",");
            sb.append("\"titulo\":\"").append(a.getTitulo()).append("\",");
            sb.append("\"duracionSegundos\":").append(a.getDuracionSegundos()).append(",");
            sb.append("\"tipo\":\"").append(a.getTipo()).append("\",");
            sb.append("\"categoria\":\"").append(a.getCategoria().name()).append("\",");
            sb.append("\"genero\":\"").append(a.getGenero()).append("\"");

            if (a instanceof Cancion) {
                Cancion c = (Cancion) a;
                sb.append(",\"artista\":\"").append(c.getArtista()).append("\",");
                sb.append("\"album\":\"").append(c.getAlbum()).append("\",");
            } else if (a instanceof EpisodioPodcast) {
                EpisodioPodcast ep = (EpisodioPodcast) a;
                sb.append(",\"anfitrion\":\"").append(ep.getAnfitrion()).append("\",");
                sb.append("\"nombrePodcast\":\"").append(ep.getNombrePodcast()).append("\",");
                sb.append("\"descripcion\":\"").append(ep.getDescripcion()).append("\",");
                sb.append("\"numeroEpisodio\":").append(ep.getNumeroEpisodio());
            }
            sb.append("}");
            if (i < lista.size() - 1) sb.append(",\n");
        }
        sb.append("\n]");
        return sb.toString();
    }

    public String crearJsonProductos(List<Producto> lista) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < lista.size(); i++) {
            Producto p = lista.get(i);
            sb.append("  {");
            sb.append("\"id\":\"").append(p.getId()).append("\",");
            sb.append("\"nombre\":\"").append(p.getNombre()).append("\",");
            sb.append("\"precio\":").append(p.getPrecio()).append(",");
            sb.append("\"descripcion\":\"").append(p.getDescripcion()).append("\",");
            sb.append("\"tipo\":\"").append(p.getTipo()).append("\"");

            if (p instanceof ArteVisualAlbum) {
                ArteVisualAlbum arte = (ArteVisualAlbum) p;
                sb.append(",\"albumAsociado\":\"").append(arte.getAlbumAsociado()).append("\",");
                sb.append("\"artista\":\"").append(arte.getArtista()).append("\",");
                sb.append("\"formato\":\"").append(arte.getFormato()).append("\"");
            } else if (p instanceof PaqueteTopTen) {
                PaqueteTopTen pq = (PaqueteTopTen) p;
                sb.append(",\"tematica\":\"").append(pq.getTematica()).append("\",");
                sb.append("\"idsCanciones\":[");
                for (int j = 0; j < pq.getIdsCanciones().size(); j++) {
                    sb.append("\"").append(pq.getIdsCanciones().get(j)).append("\"");
                    if (j < pq.getIdsCanciones().size() - 1) sb.append(",");
                }
                sb.append("]");
            }
            sb.append("}");
            if (i < lista.size() - 1) sb.append(",\n");
        }
        sb.append("\n]");
        return sb.toString();
    }

    public String crearJsonUsuarios(List<Usuario> lista) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < lista.size(); i++) {
            Usuario u = lista.get(i);
            sb.append("  {");
            sb.append("\"id\":\"").append(u.getId()).append("\",");
            sb.append("\"nombre\":\"").append(u.getNombre()).append("\",");
            sb.append("\"correo\":\"").append(u.getCorreo()).append("\",");
            sb.append("\"saldo\":").append(u.getSaldo()).append(",");
            sb.append("\"rol\":\"").append(u.getRolUsuario().name()).append("\",");
            sb.append("\"controlParental\":\"").append(u.isControlParental()).append("\",");
            sb.append("\"edad\":").append(u.getEdad()).append(",");

            // Estadisticas
            sb.append("\"estadisticas\":{");
            sb.append("\"tiempoEscuchaSegundos\":").append(u.getEstadisticas().getTiempoEscuchaSegundos()).append(",");
            sb.append("\"cancionesEnBiblioteca\":").append(u.getEstadisticas().getCancionesEnBiblioteca()).append(",");
            sb.append("\"comprasRealizadas\":").append(u.getEstadisticas().getComprasRealizadas()).append(",");
            sb.append("\"reproduccionesTotales\":").append(u.getEstadisticas().getReproduccionesTotales());
            sb.append("},");

            // Historial de compras
            sb.append("\"historialCompras\":").append(crearJsonCompras(u.getHistorialCompras())).append(",");

            // Biblioteca y Playlists
            sb.append("\"biblioteca\":{");
            sb.append("\"playlists\":[\n");
            List<Playlist> playlists = u.getBiblioteca().getPlaylists();
            for (int j = 0; j < playlists.size(); j++) {
                Playlist pl = playlists.get(j);
                sb.append("      {");
                sb.append("\"id\":\"").append(pl.getId()).append("\",");
                sb.append("\"nombre\":\"").append(pl.getNombre()).append("\",");
                sb.append("\"aliasPropietario\":\"").append(pl.getAliasPropietario()).append("\",");
                sb.append("\"contenido\":").append(crearJsonAudios(pl.getContenido()));
                sb.append("}");
                if (j < playlists.size() - 1) sb.append(",");
            }
            sb.append("    ]");
            sb.append("}");

            sb.append("}");
            if (i < lista.size() - 1) sb.append(",\n");
        }
        sb.append("\n]");
        return sb.toString();
    }

    public String crearJsonCompras(List<Compra> lista) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < lista.size(); i++) {
            Compra c = lista.get(i);
            sb.append("{");
            sb.append("\"idProducto\":\"").append(c.getIdProducto()).append("\",");
            sb.append("\"aliasUsuario\":\"").append(c.getAliasUsuario()).append("\",");
            sb.append("\"montoPagado\":").append(c.getMontoPagado()).append(",");
            sb.append("\"fecha\":\"").append(c.getFecha().toString()).append("\"");
            sb.append("}");
            if (i < lista.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}