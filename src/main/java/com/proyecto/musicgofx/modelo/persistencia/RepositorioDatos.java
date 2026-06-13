package com.proyecto.musicgofx.modelo.persistencia;

import com.proyecto.musicgofx.modelo.entidades.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase actúa como el puente entre la lógica de la aplicación y el almacenamiento en disco.
 * Se encarga de leer y escribir los archivos JSON utilizando el Parser y el Writer.
 */
@SuppressWarnings("SpellCheckingInspection")
public class RepositorioDatos {

    // Rutas de los archivos de datos
    private static final String DIRECTORIO_DATOS = "data/";
    private static final String ARCHIVO_AUDIOS = DIRECTORIO_DATOS + "canciones.json";
    private static final String ARCHIVO_CATALOGO = DIRECTORIO_DATOS + "catalogo.json";
    private static final String ARCHIVO_PRODUCTOS = DIRECTORIO_DATOS + "productos.json";
    private static final String ARCHIVO_USUARIOS = DIRECTORIO_DATOS + "usuarios.json";
    private static final String ARCHIVO_ADMINISTRADORES = DIRECTORIO_DATOS + "administradores.json";


    // Herramientas para convertir de Texto a Objeto (Parser) y de Objeto a Texto (Writer)
    private final JsonParser parser;
    private final JsonWriter writer;

    /**
     * Constructor: Inicializa las herramientas de JSON y asegura que la carpeta 'data' exista.
     */
    public RepositorioDatos() {
        this.parser = new JsonParser();
        this.writer = new JsonWriter();
        crearDirectorioSiNoExiste();
    }

    /**
     * Verifica si existe la carpeta donde se guardan los archivos.
     * Si no existe, la crea automáticamente para evitar errores de "Archivo no encontrado".
     */
    private void crearDirectorioSiNoExiste() {
        try {
            Path path = Paths.get(DIRECTORIO_DATOS);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (Exception e) {
            System.err.println("Error crítico: No se pudo crear la carpeta de datos: " + e.getMessage());
        }
    }

    /**
     * Lee el archivo 'canciones.json' y lo convierte en una lista de objetos Audio.
     *
     * @return Una lista de canciones cargadas o una lista vacía si el archivo no existe.
     */
    public List<Audio> cargarAudios() {
        Path path = Paths.get(ARCHIVO_AUDIOS);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        try {
            String contenido = Files.readString(path);
            if (contenido.isBlank()) return new ArrayList<>();
            // El parser procesa el texto y nos devuelve objetos Java
            return parser.parsearAudios(contenido);
        } catch (Exception e) {
            System.err.println("Error al leer canciones.json: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Lee el archivo 'catalogo.json' (que contiene tanto canciones como podcasts).
     *
     * @return Lista de audios del catálogo global.
     */
    public List<Audio> cargarCatalogo() {
        Path path = Paths.get(ARCHIVO_CATALOGO);
        if (!Files.exists(path)) {
            System.out.println("[Info] Archivo de catálogo no encontrado, iniciando vacío.");
            return new ArrayList<>();
        }
        try {
            String contenido = Files.readString(path);
            if (contenido.isBlank()) return new ArrayList<>();
            // Usamos parsearAudios porque el catálogo contiene objetos que heredan de Audio
            return parser.parsearAudios(contenido);
        } catch (Exception e) {
            System.err.println("Error al leer catalogo.json: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Lee el archivo 'productos.json' para obtener los items de la tienda (artes, pases VIP, etc).
     *
     * @return Lista de productos disponibles para la compra.
     */
    public List<Producto> cargarProductos() {
        Path path = Paths.get(ARCHIVO_PRODUCTOS);
        if (!Files.exists(path)) return new ArrayList<>();

        try {
            String contenido = Files.readString(path);
            if (contenido.isBlank()) return new ArrayList<>();
            return parser.parsearProductos(contenido);
        } catch (Exception e) {
            System.err.println("Error al cargar productos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Recupera la base de datos de usuarios, incluyendo sus bibliotecas y estadísticas.
     *
     * @return Lista de usuarios registrados.
     */
    public List<Usuario> cargarUsuarios() {
        Path path = Paths.get(ARCHIVO_USUARIOS);
        if (!Files.exists(path)) return new ArrayList<>();

        try {
            String contenido = Files.readString(path);
            if (contenido.isBlank()) return new ArrayList<>();
            return parser.parsearUsuarios(contenido);
        } catch (Exception e) {
            System.err.println("Error al cargar usuarios: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Usuario> cargarAdministradores(){
        Path path = Paths.get(ARCHIVO_ADMINISTRADORES);
        if (!Files.exists(path)) return new ArrayList<>();

        try {
            String contenido = Files.readString(path);
            if (contenido.isBlank()) return new ArrayList<>();
            return parser.parsearUsuarios(contenido);
        } catch (Exception e) {
            System.err.println("Error al cargar usuarios: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    /**
     * Guarda la lista actual de usuarios en el archivo 'usuarios.json'.
     * Este método se debe llamar cada vez que un usuario compra algo o crea una playlist.
     *
     * @param usuarios La lista de usuarios con los datos actualizados que queremos salvar.
     */
    public void guardarUsuarios(List<Usuario> usuarios) {
        Path path = Paths.get(ARCHIVO_USUARIOS);
        try {
            String json = writer.crearJsonUsuarios(usuarios);
            Files.writeString(path, json);
        } catch (Exception e) {
            System.err.println("Error al intentar guardar los usuarios: " + e.getMessage());
        }
    }

    /**
     * Guarda la lista actual de usuarios con el rol administrador en el archivo 'administradores.json'.
     *  La lista de usuarios con los datos actualizados que queremos salvar.
     */
    public void guardarAdmin(List<Usuario> administradores) {
        Path path = Paths.get(ARCHIVO_ADMINISTRADORES);
        try {
            String json = writer.crearJsonUsuarios(administradores);
            Files.writeString(path, json);
        } catch (Exception e) {
            System.err.println("Error al intentar guardar los usuarios: " + e.getMessage());
        }
    }
}