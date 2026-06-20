package com.proyecto.musicgofx.modelo.servicios;

import com.proyecto.musicgofx.modelo.entidades.Audio;
import com.proyecto.musicgofx.modelo.entidades.Playlist;
import com.proyecto.musicgofx.modelo.entidades.Usuario;
import com.proyecto.musicgofx.modelo.entidades.Mensaje;
import com.proyecto.musicgofx.excepciones.ContenidoNoEncontradoException;
import com.proyecto.musicgofx.excepciones.ContenidoRestringidoException;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import  javafx.beans.property.SimpleStringProperty;


import java.util.ArrayList;
import java.util.List;

/**
 * Coordina la reproduccion de contenidos, mantiene el estado de la cola
 * de reproduccion y actualiza las estadisticas del usuario.
 *
 * @author Equipo MusicGO
 */
public class GestorReproduccion {

    private final GestorUsuarios gestorUsuarios;
    private final GestorCatalogo gestorCatalogo;
    private StringProperty mensajeAlerta = new SimpleStringProperty("");

    // --- NUEVAS VARIABLES DE ESTADO PARA LA BARRA DE REPRODUCCIÓN ---
    private final ObjectProperty<Audio> audioActual = new SimpleObjectProperty<>();
    private List<Audio> colaReproduccion;
    private int indiceActual;

    /**
     * Inicializa el gestor de reproduccion con los servicios de usuarios y catalogo.
     */
    public GestorReproduccion(GestorUsuarios gestorUsuarios, GestorCatalogo gestorCatalogo) {
        this.gestorUsuarios = gestorUsuarios;
        this.gestorCatalogo = gestorCatalogo;
        this.colaReproduccion = new ArrayList<>();
        this.indiceActual = -1;
    }

    // ════════════════════════════════════════════════════
    //   CONTROLES DE LA BARRA DE REPRODUCCIÓN
    // ════════════════════════════════════════════════════

    /**
     * Carga una lista de canciones (cola) y reproduce desde un índice específico.
     * Ideal para cuando el usuario hace clic en una canción del Explorador.
     */
    public void iniciarColaDeReproduccion(List<Audio> nuevaCola, int indiceInicial, Usuario usuario) {
        if (nuevaCola == null || nuevaCola.isEmpty() || indiceInicial < 0 || indiceInicial >= nuevaCola.size()) {
            return;
        }
        this.colaReproduccion = nuevaCola;
        this.indiceActual = indiceInicial;
        ejecutarAudioActual(usuario);
    }

    /**
     * Avanza a la siguiente canción en la cola.
     */
    public boolean siguiente(Usuario usuario) {
        if (colaReproduccion != null && indiceActual < colaReproduccion.size() - 1) {
            indiceActual++;
            ejecutarAudioActual(usuario);
            return true;
        }
        return false;
    }

    /**
     * Retrocede a la canción anterior en la cola.
     */
    public boolean anterior(Usuario usuario) {
        if (colaReproduccion != null && indiceActual > 0) {
            indiceActual--;
            ejecutarAudioActual(usuario);
            return true;
        }
        return false;
    }

    /**
     * Devuelve el audio que está sonando actualmente para que la interfaz gráfica (MainController)
     * pueda obtener el Título, Artista, etc. y pintarlo en pantalla.
     */
    public ObjectProperty<Audio> audioActualProperty() {
        return audioActual;
    }


    /**
     * Método privado centralizado que hace la acción de reproducir y sumar estadísticas.
     * Evita repetir código en Siguiente, Anterior e Iniciar.
     */
    private void ejecutarAudioActual(Usuario usuario) {
        Audio audioCandidato = colaReproduccion.get(indiceActual);
        if (audioCandidato == null) return;
        String categoriaTexto = String.valueOf(audioCandidato.getCategoria());

        if (usuario != null && usuario.isControlParental() && "MAYOR".equalsIgnoreCase(categoriaTexto)) {
            mensajeAlerta.set("El audio '" + audioCandidato.getTitulo() + "' está bloqueado por el Control Parental.");
            System.out.println("[Control Parental] Canción bloqueada. Saltando a la siguiente...");
            siguiente(usuario);
            return;
        }

        this.audioActual.set(audioCandidato);
        audioActual.get().reproducir();

        if (usuario != null) {
            usuario.getEstadisticas().sumarTiempoEscucha(audioActual.get().getDuracionSegundos());
            gestorUsuarios.guardarCambios();
            System.out.println("[Sistema] Estadísticas de '" + usuario.getNombre() + "' actualizadas.");
        }
    }

    /**
     * Reproduce una playlist completa, cargándola en la cola de reproducción.
     */
    public void reproducirPlaylist(String idUsuarioOAlias, String idPlaylist) {
        Usuario usuario = gestorUsuarios.buscarPorIdOAlias(idUsuarioOAlias);
        if (usuario == null) return;

        Playlist playlist = usuario.getBiblioteca().buscarPorId(idPlaylist);
        if (playlist == null || playlist.getContenido().isEmpty()) return;

        iniciarColaDeReproduccion(playlist.getContenido(), 0, usuario);
    }

    /**
     * Método clásico (Mantiene compatibilidad con tu código anterior).
     */
    public Mensaje reproducirAudio(Usuario usuario, String idAudioOTitulo) throws ContenidoNoEncontradoException, ContenidoRestringidoException {
        Audio audioEncontrado = null;

        for (Audio a : gestorCatalogo.getTodosLosAudios()) {
            if (a.getId().equalsIgnoreCase(idAudioOTitulo) || a.getTitulo().equalsIgnoreCase(idAudioOTitulo)) {
                audioEncontrado = a;
                break;
            }
        }

        if (audioEncontrado == null) {
            throw new ContenidoNoEncontradoException("El contenido '" + idAudioOTitulo + "' no existe en el catalogo.");
        }

        if (audioEncontrado.getCategoria() == Audio.Clasificacion.MAYOR) {
            if (usuario.isControlParental() || usuario.getEdad() < 18) {
                throw new ContenidoRestringidoException("Acceso denegado: El contenido '" + audioEncontrado.getTitulo() +
                        "' es para mayores de edad y tu cuenta tiene restricciones (Edad: " + usuario.getEdad() + ").");
            }
        }


        List<Audio> listaSola = new ArrayList<>();
        listaSola.add(audioEncontrado);
        iniciarColaDeReproduccion(listaSola, 0, usuario);

        return new Mensaje("Sistema", usuario.getNombre(), Mensaje.Tipo.CONFIRMACION, "▶ Reproduciendo: " + audioEncontrado.getTitulo());
    }
    public StringProperty mensajeAlertaProperty() {
        return mensajeAlerta;
    }
    public java.util.List<Audio> getTodosLosAudios() {
        return gestorCatalogo.getTodosLosAudios();
    }
}