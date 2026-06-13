package com.proyecto.musicgofx.modelo.servicios;

import com.proyecto.musicgofx.modelo.entidades.*;
import com.proyecto.musicgofx.modelo.persistencia.RepositorioDatos;

/**
 * Gestor encargado de centralizar todas las operaciones exclusivas del
 * perfil de Administrador, tales como el mantenimiento del catálogo de audios,
 * de productos de la tienda y la modificación de roles de usuarios.
 * * @author Equipo MusicGO
 */
public class GestorAdministrador {

    private final GestorCatalogo gestorCatalogo;
    private final GestorUsuarios gestorUsuarios;
    private final RepositorioDatos repositorio;

    /**
     * Constructor del GestorAdministrador.
     * Recibe e integra las dependencias de los otros gestores y el repositorio global.
     *
     * @param gestorCatalogo Instancia compartida del catálogo global.
     * @param gestorUsuarios Instancia compartida del gestor de usuarios.
     * @param repositorio    Instancia del repositorio único de persistencia.
     */
    public GestorAdministrador(GestorCatalogo gestorCatalogo, GestorUsuarios gestorUsuarios, RepositorioDatos repositorio) {
        this.gestorCatalogo = gestorCatalogo;
        this.gestorUsuarios = gestorUsuarios;
        this.repositorio = repositorio;
    }

    /**
     * Método auxiliar privado para validar la seguridad del sistema.
     * Centraliza la verificación de rol para evitar duplicar código en cada función.
     *
     * @param u Usuario que intenta realizar la operación.
     * @throws SecurityException Si el usuario no tiene permisos válidos.
     */
    private void validarAdministrador(Usuario u) {
        if (u == null || u.getRolUsuario() != Usuario.RolUsuario.ADMINISTRADOR) {
            throw new SecurityException("Acceso denegado: Se requieren permisos de ADMINISTRADOR.");
        }
    }



    /**
     * Añade un nuevo audio (Canción o Podcast) al catálogo general y lo persiste.
     */
    public void agregarAudio(Usuario usuarioActual, Audio nuevoAudio) {
        validarAdministrador(usuarioActual);

        if (nuevoAudio == null) {
            System.err.println("Error: El audio a agregar no puede ser nulo.");
            return;
        }
        gestorCatalogo.getTodosLosAudios().add(nuevoAudio);
        if (nuevoAudio instanceof Cancion) {
            System.out.println(" > [Éxito] Nueva CANCIÓN agregada: " + nuevoAudio.getTitulo() + " (ID: " + nuevoAudio.getId() + ")");
        } else if (nuevoAudio instanceof EpisodioPodcast) {
            System.out.println(" > [Éxito] Nuevo PODCAST agregado: " + nuevoAudio.getTitulo() + " (ID: " + nuevoAudio.getId() + ")");
        }

        repositorio.guardarUsuarios(gestorUsuarios.getUsuarios());
    }

    /**
     * Elimina un audio del catálogo global usando su ID único.
     */
    public void quitarAudio(Usuario usuarioActual, String idAudio) {
        validarAdministrador(usuarioActual);
        boolean removido = gestorCatalogo.getTodosLosAudios().removeIf(audio -> audio.getId().equalsIgnoreCase(idAudio));

        if (removido) {
            System.out.println(" > [Éxito] El audio con ID '" + idAudio + "' fue eliminado del catálogo.");
        } else {
            System.err.println("Error: No se encontró ningún audio con el ID '" + idAudio + "' en el catálogo.");
        }
    }

    /**
     * Modifica las propiedades esenciales de un audio a través de su identificador.
     */
    public void modificarAudio(Usuario usuarioActual, String idAudio, String nuevoTitulo) {
        validarAdministrador(usuarioActual);

        Audio audioEncontrado = gestorCatalogo.buscarAudioPorId(idAudio);

        if (audioEncontrado != null) {
            String tituloAnterior = audioEncontrado.getTitulo();
            audioEncontrado.setTitulo(nuevoTitulo);
            System.out.println(" > [Éxito] Audio modificado. De: '" + tituloAnterior + "' A: '" + nuevoTitulo + "'");
        } else {
            System.err.println("Error: No se localizó el audio con ID '" + idAudio + "' para modificar.");
        }
    }


    /**
     * Introduce un nuevo producto (ArteVisual o PaqueteTopTen) a la tienda virtual.
     */
    public void agregarProducto(Usuario usuarioActual, Producto nuevoProducto) {
        validarAdministrador(usuarioActual);

        if (nuevoProducto == null) {
            System.err.println("Error: El producto a registrar no puede ser nulo.");
            return;
        }
        gestorCatalogo.getTodosLosProductos().add(nuevoProducto);
        System.out.println(" > [Éxito] Nuevo producto disponible en la tienda: " + nuevoProducto.getNombre());
    }

    /**
     * Retira un producto de la tienda para que ya no pueda ser adquirido.
     */
    public void quitarProducto(Usuario usuarioActual, String idProducto) {
        validarAdministrador(usuarioActual);
        boolean removido = gestorCatalogo.getTodosLosProductos().removeIf(prod -> prod.getId().equalsIgnoreCase(idProducto));
        if (removido) {
            System.out.println(" > [Éxito] El producto con ID '" + idProducto + "' fue retirado de la tienda.");
        } else {
            System.err.println("Error: No se encontró ningún producto con el ID '" + idProducto + "'.");
        }
    }

    /**
     * Modifica el precio de venta de un artículo del catálogo de la tienda.
     */
    public void modificarProducto(Usuario usuarioActual, String idProducto, double nuevoPrecio) {
        validarAdministrador(usuarioActual);
        if (nuevoPrecio < 0) {
            System.err.println("Error: El precio de un producto no puede ser negativo.");
            return;
        }
        Producto productoEncontrado = gestorCatalogo.buscarProductoPorIdONombre(idProducto);
        if (productoEncontrado != null) {
            productoEncontrado.setPrecio(nuevoPrecio);
            System.out.println(" > [Éxito] El precio del producto '" + productoEncontrado.getNombre() + "' se actualizó a: $" + nuevoPrecio);
        } else {
            System.err.println("Error: El producto con ID o Nombre '" + idProducto + "' no existe.");
        }
    }



    /**
     * Permite cambiar el nivel de privilegios de un usuario en el sistema.
     * Puede ascender a un usuario normal a ADMINISTRADOR o revocar permisos.
     */
    public void modificarRolUsuario(Usuario usuarioActual, String aliasUsuarioAModificar, Usuario.RolUsuario nuevoRol) {
        validarAdministrador(usuarioActual);
        Usuario usuarioDestino = gestorUsuarios.buscarPorIdOAlias(aliasUsuarioAModificar);
        if (usuarioDestino != null) {
            usuarioDestino.setRolUsuario(nuevoRol);
            System.out.println(" > [Éxito] Permisos actualizados. El usuario '" + usuarioDestino.getNombre() + "' ahora tiene el rol: " + nuevoRol.name());
            gestorUsuarios.guardarCambios();
        } else {
            System.err.println("Error: No se encontró ningún usuario con el alias '" + aliasUsuarioAModificar + "'.");
        }
    }
}