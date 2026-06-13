package com.proyecto.musicgofx.modelo.servicios;

import com.proyecto.musicgofx.modelo.entidades.Audio;
import com.proyecto.musicgofx.modelo.entidades.Playlist;
import com.proyecto.musicgofx.modelo.entidades.Usuario;
import com.proyecto.musicgofx.modelo.entidades.Mensaje;
import com.proyecto.musicgofx.excepciones.ContenidoNoEncontradoException;
import com.proyecto.musicgofx.excepciones.ContenidoRestringidoException;

/**
 * Coordina la reproduccion de contenidos y la actualizacion
 * automatica de las estadisticas de escucha del usuario.
 *
 * @author Equipo MusicGO
 */
public class GestorReproduccion {

    private final GestorUsuarios gestorUsuarios;
    private final GestorCatalogo gestorCatalogo;

    /**
     * Inicializa el gestor de reproduccion con los servicios de usuarios y catalogo.
     *
     * @param gestorUsuarios Servicio de gestion de usuarios.
     * @param gestorCatalogo Servicio de gestion del catalogo global.
     */
    public GestorReproduccion(GestorUsuarios gestorUsuarios, GestorCatalogo gestorCatalogo) {
        this.gestorUsuarios = gestorUsuarios;
        this.gestorCatalogo = gestorCatalogo;
    }

    /**
     * Reproduce un audio individual del catalogo y actualiza las estadisticas del usuario.
     *
     * @param idUsuarioOAlias Identificador o alias del usuario.
     * @param idAudioOTitulo Identificador o titulo del audio a reproducir.
     */
    public void reproducirAudio(String idUsuarioOAlias, String idAudioOTitulo) {
        Usuario usuario = gestorUsuarios.buscarPorIdOAlias(idUsuarioOAlias);
        if (usuario == null) {
            System.err.println("Error: No se encontro el usuario para iniciar reproduccion.");
            return;
        }

        Audio audioEncontrado = null;
        for (Audio a : gestorCatalogo.getTodosLosAudios()) {
            if (a.getId().equalsIgnoreCase(idAudioOTitulo) || a.getTitulo().equalsIgnoreCase(idAudioOTitulo)) {
                audioEncontrado = a;
                break;
            }
        }

        if (audioEncontrado == null) {
            System.err.println("Error: El contenido '" + idAudioOTitulo + "' no existe en el catalogo.");
            return;
        }

        audioEncontrado.reproducir();
        usuario.getEstadisticas().sumarTiempoEscucha(audioEncontrado.getDuracionSegundos());
        gestorUsuarios.guardarCambios();

        System.out.println("[Sistema] Estadisticas de '" + usuario.getNombre() + "' actualizadas.");
    }

    /**
     * Reproduce una playlist completa del usuario procesando cada audio individualmente.
     *
     * @param idUsuarioOAlias Identificador o alias del usuario.
     * @param idPlaylist Identificador de la playlist a reproducir.
     */
    public void reproducirPlaylist(String idUsuarioOAlias, String idPlaylist) {
        Usuario usuario = gestorUsuarios.buscarPorIdOAlias(idUsuarioOAlias);
        if (usuario == null) {
            System.err.println("Error: Usuario no encontrado.");
            return;
        }

        Playlist playlist = usuario.getBiblioteca().buscarPorId(idPlaylist);
        if (playlist == null) {
            System.err.println("Error: La playlist no existe en tu biblioteca.");
            return;
        }

        if (playlist.getContenido().isEmpty()) {
            System.out.println("La playlist '" + playlist.getNombre() + "' esta vacia.");
            return;
        }

        System.out.println("\n>>> Iniciando Playlist: " + playlist.getNombre() + " <<<");

        for (Audio a : playlist.getContenido()) {
            a.reproducir();
            usuario.getEstadisticas().sumarTiempoEscucha(a.getDuracionSegundos());
        }

        gestorUsuarios.guardarCambios();

        System.out.println(">>> Fin de la reproduccion de la lista <<<\n");
    }

    /**
     * Metodo principal utilizado por el menu interactivo.
     * Busca el audio en el catalogo, lo reproduce, actualiza estadisticas y persiste los datos.
     *
     * @param usuario Instancia del usuario que solicita la reproduccion.
     * @param idAudioOTitulo Criterio de busqueda del audio (ID o Titulo).
     * @return Mensaje de exito con el titulo del audio reproducido.
     * @throws ContenidoNoEncontradoException Si el audio no se encuentra en el catalogo general.
     */
    public Mensaje reproducir(Usuario usuario, String idAudioOTitulo) throws ContenidoNoEncontradoException {
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
                        "    ' es para mayores de edad y tu cuenta tiene restricciones (Edad: " + usuario.getEdad() + ").");
            }
        }
        audioEncontrado.reproducir();
        usuario.getEstadisticas().sumarTiempoEscucha(audioEncontrado.getDuracionSegundos());

        gestorUsuarios.guardarCambios();
        return new Mensaje("Sistema", usuario.getNombre(), Mensaje.Tipo.CONFIRMACION, "▶ Reproduciendo con exito: " + audioEncontrado.getTitulo());
    }

    public java.util.List<Audio> getTodosLosAudios() {
        return gestorCatalogo.getTodosLosAudios();
    }
}