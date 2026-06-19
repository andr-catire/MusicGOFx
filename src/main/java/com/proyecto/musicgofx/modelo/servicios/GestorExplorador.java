package com.proyecto.musicgofx.modelo.servicios;
import com.proyecto.musicgofx.modelo.servicios.GestorCatalogo;
import com.proyecto.musicgofx.modelo.entidades.Audio;
import com.proyecto.musicgofx.modelo.entidades.Cancion;
import com.proyecto.musicgofx.modelo.entidades.EpisodioPodcast;

import java.util.ArrayList;
import java.util.List;

public class GestorExplorador {

    private boolean cumpleFiltroTipo(Audio audio, String tipo) {
        if (tipo.equalsIgnoreCase("Todos")) {
            return true;
        }
        if (tipo.equalsIgnoreCase("Canciones") && audio instanceof Cancion) {
            return true;
        }
        if (tipo.equalsIgnoreCase("Podcasts") && audio instanceof EpisodioPodcast) {
            return true;
        }
        return false;
    }
    private boolean cumpleFiltroGenero(Audio audio, String genero) {
        if (genero.equalsIgnoreCase("Todos") ||
                genero.equalsIgnoreCase("Todos los géneros") ||
                genero.equalsIgnoreCase("Generos")) {
            return true;
        }
        return audio.getGenero() != null && audio.getGenero().equalsIgnoreCase(genero);
    }

    private boolean cumpleFiltroTexto(Audio audio, String busqueda) {
        if (busqueda.isEmpty()) {
            return true;
        }
        if (audio.getTitulo().toLowerCase().contains(busqueda)) {
            return true;
        }
        if (audio instanceof Cancion) {
            return ((Cancion) audio).getArtista().toLowerCase().contains(busqueda);
        }

        if (audio instanceof EpisodioPodcast) {
            return ((EpisodioPodcast) audio).getAnfitrion().toLowerCase().contains(busqueda);
        }
        return false;
    }

    public List<Audio> filtrarCatalogo(List<Audio> catalogoCompleto, String textoBusqueda, String tipoAudio, String generoEsperado) {
        List<Audio> resultados = new ArrayList<>();

        String busqueda = (textoBusqueda == null) ? "" : textoBusqueda.trim().toLowerCase();
        String tipo = (tipoAudio == null || tipoAudio.isEmpty()) ? "Todos" : tipoAudio;
        String genero = (generoEsperado == null || generoEsperado.isEmpty()) ? "Todos los géneros" : generoEsperado;


        for (Audio audio : catalogoCompleto) {

            if (!cumpleFiltroTipo(audio, tipo)) {
                continue;
            }

            if (!cumpleFiltroGenero(audio, genero)) {
                continue;
            }

            if (!cumpleFiltroTexto(audio, busqueda)) {
                continue;
            }
            resultados.add(audio);
        }

        return resultados;
    }
}

