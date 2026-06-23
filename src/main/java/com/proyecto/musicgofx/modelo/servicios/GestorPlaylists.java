package com.proyecto.musicgofx.modelo.servicios;

import com.proyecto.musicgofx.modelo.entidades.Audio;
import com.proyecto.musicgofx.modelo.entidades.Playlist;
import com.proyecto.musicgofx.modelo.entidades.Usuario;
import com.proyecto.musicgofx.excepciones.UsuarioNoEncontradoException;
import com.proyecto.musicgofx.excepciones.PlaylistNoEncontradaException;
import com.proyecto.musicgofx.excepciones.ErrorCompartirPlaylistException;

/**
 * Administra las listas de reproducción de los usuarios, gestionando su creación,
 * modificación de contenido y sincronización con el catálogo global.
 */
public class GestorPlaylists {

    private GestorUsuarios gestorUsuarios;
    private GestorCatalogo gestorCatalogo;

    public GestorPlaylists(GestorUsuarios gestorUsuarios, GestorCatalogo gestorCatalogo) {
        this.gestorUsuarios = gestorUsuarios;
        this.gestorCatalogo = gestorCatalogo;
    }

    /**
     * Crea una nueva playlist para el usuario logueado en la interfaz.
     * @return true si se creó correctamente, false si ya existía una con ese nombre.
     */
    public boolean crearPlaylistParaUsuario(Usuario usuario, String nombre) {
        if (usuario != null) {
            Playlist nueva = new Playlist(nombre, usuario.getNombre());
            if (usuario.getBiblioteca().agregarPlaylist(nueva)) {
                gestorUsuarios.guardarCambios();
                if (usuario.getRolUsuario() == Usuario.RolUsuario.ADMINISTRADOR) {
                    gestorUsuarios.guardarAdmin();
                }
                return true;

            }
        }
        return false;
    }

    /**
     * Añade un audio específico a una playlist existente de un usuario.
     * Ideal para el botón "+" de la interfaz gráfica.
     */
    public boolean agregarAudioAPlaylist(Usuario usuario, String nombrePlaylist, Audio audioNuevo) {
        if (usuario != null && usuario.getBiblioteca() != null) {
            for (Playlist p : usuario.getBiblioteca().getPlaylists()) {
                if (p.getNombre().equalsIgnoreCase(nombrePlaylist)) {
                    boolean agregado = p.agregarAudio(audioNuevo);
                    if (agregado) {
                        usuario.refrescarConteoBiblioteca();
                        gestorUsuarios.guardarCambios();
                    }
                    return agregado;
                }
            }
        }
        return false;
    }

    /**
     * Crea una nueva playlist buscando al usuario por su ID o Alias.
     */
    public void crearPlaylist(String idUsuarioOAlias, String nombre) {
        Usuario usuario = gestorUsuarios.buscarPorIdOAlias(idUsuarioOAlias);
        if (usuario != null) {
            boolean exito = crearPlaylistParaUsuario(usuario, nombre);
            if (exito) {
                System.out.println("Playlist '" + nombre + "' creada con éxito.");
            } else {
                System.err.println("Error: Ya existe una playlist con ese identificador.");
            }
        } else {
            System.err.println("Usuario no encontrado.");
        }
    }

    /**
     * Agrega un audio del catálogo a una playlist específica del usuario usando IDs.
     */
    public void agregarAudioAPlaylist(String idUsuarioOAlias, String idPlaylist, String idAudio) {
        Usuario usuario = gestorUsuarios.buscarPorIdOAlias(idUsuarioOAlias);
        Audio audio = gestorCatalogo.buscarAudioPorId(idAudio);

        if (usuario != null && audio != null) {
            Playlist playlist = usuario.getBiblioteca().buscarPorId(idPlaylist);
            if (playlist != null) {
                if (playlist.agregarAudio(audio)) {
                    System.out.println("Agregado: " + audio.getTitulo() + " -> " + playlist.getNombre());
                    usuario.refrescarConteoBiblioteca();
                    gestorUsuarios.guardarCambios();
                } else {
                    System.out.println("El audio ya se encuentra en la playlist.");
                }
            } else {
                System.err.println("Playlist no encontrada.");
            }
        } else {
            System.err.println("Usuario o Audio no existen.");
        }
    }

    /**
     * Remueve un audio específico de una playlist de un usuario y guarda los cambios.
     */
    public void removerAudioDePlaylist(String idUsuarioOAlias, String idPlaylist, String idAudio) throws PlaylistNoEncontradaException {
        Usuario usuario = gestorUsuarios.buscarPorIdOAlias(idUsuarioOAlias);
        if (usuario == null) {
            System.out.println("Error: Usuario no encontrado.");
            return;
        }

        Playlist playlist = usuario.getBiblioteca().buscarPorId(idPlaylist);
        if (playlist == null) {
            throw new PlaylistNoEncontradaException("Error: No se encontró la playlist con ID '" + idPlaylist + "'.");
        }

        boolean eliminado = playlist.getContenido().removeIf(audio -> audio.getId().equals(idAudio));

        if (eliminado) {
            System.out.println("¡Audio removido exitosamente de '" + playlist.getNombre() + "'!");
            usuario.refrescarConteoBiblioteca();
            gestorUsuarios.guardarCambios();
        } else {
            System.out.println("Aviso: Ese audio no se encontraba en la playlist.");
        }
    }

    /**
     * Elimina una playlist completa de la biblioteca.
     */
    public boolean eliminarPlaylist(Usuario usuario, String nombrePlaylist) {
        if (usuario != null && usuario.getBiblioteca() != null) {
            Playlist playlistAELiminar = null;
            for (Playlist p : usuario.getBiblioteca().getPlaylists()) {
                if (p.getNombre().equalsIgnoreCase(nombrePlaylist)) {
                    playlistAELiminar = p;
                    break;
                }
            }

            if (playlistAELiminar != null) {
                usuario.getBiblioteca().getPlaylists().remove(playlistAELiminar);
                System.out.println("Playlist '" + nombrePlaylist + "' eliminada correctamente.");
                usuario.refrescarConteoBiblioteca();
                gestorUsuarios.guardarCambios();

                if (usuario.getRolUsuario() == Usuario.RolUsuario.ADMINISTRADOR) {
                    gestorUsuarios.guardarAdmin();
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Muestra el contenido detallado de una playlist específica por consola.
     */
    public void mostrarContenidoPlaylist(String idUsuarioOAlias, String idPlaylist) {
        Usuario usuario = gestorUsuarios.buscarPorIdOAlias(idUsuarioOAlias);
        if (usuario != null) {
            Playlist p = usuario.getBiblioteca().buscarPorId(idPlaylist);
            if (p != null) {
                System.out.println("\n--- Playlist: " + p.getNombre() + " ---");
                if (p.getContenido().isEmpty()) {
                    System.out.println(" (La playlist está vacía)");
                } else {
                    for (Audio a : p.getContenido()) {
                        System.out.println(" > " + a.toString());
                    }
                }
            } else {
                System.err.println("Playlist no encontrada.");
            }
        } else {
            System.err.println("Usuario no encontrado.");
        }
    }

    public java.util.List<Audio> getTodosLosAudios() {
        return gestorCatalogo.getTodosLosAudios();
    }

    /**
     * Realiza una copia exacta de una playlist existente en la biblioteca del usuario activo
     * y la transfiere a la biblioteca del usuario destinatario.
     */
    public void compartirPlaylist(Usuario usuarioActivo, String aliasDestinatario, String idPlaylist) throws UsuarioNoEncontradoException, PlaylistNoEncontradaException, ErrorCompartirPlaylistException {
        Usuario usuariodestinatario = gestorUsuarios.buscarPorIdOAlias(aliasDestinatario);
        if (usuariodestinatario == null) {
            throw new UsuarioNoEncontradoException("Usuario no encontrado.");
        }

        Playlist playlist = usuarioActivo.getBiblioteca().buscarPorId(idPlaylist);
        if (playlist == null) {
            throw new PlaylistNoEncontradaException("Error: No se encontró la playlist con ID '" + idPlaylist + "'.");
        }

        String playlistid = playlist.getId();
        if (playlistid.toUpperCase().startsWith("PRD")) {
            throw new ErrorCompartirPlaylistException("ERROR: La playlist que intentas compartir es un paquete Top Ten de pago.");
        }

        Playlist copiaPlaylist = new Playlist(playlist.getNombre() + " Compartida", usuariodestinatario.getNombre());

        for (Audio audio : playlist.getContenido()) {
            copiaPlaylist.agregarAudio(audio);
        }

        usuariodestinatario.getBiblioteca().agregarPlaylist(copiaPlaylist);
        gestorUsuarios.guardarCambios();
    }

}