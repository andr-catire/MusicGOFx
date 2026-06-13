package com.proyecto.musicgofx.modelo.persistencia;

import com.proyecto.musicgofx.modelo.entidades.*;
import java.time.LocalDateTime;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase encargada de analizar (parsear) cadenas de texto en formato JSON
 * y convertirlas en objetos del modelo del sistema de forma manual.
 */
public class JsonParser {

    /**
     * Lee un archivo desde una ruta especifica y devuelve su contenido como cadena de texto.
     */
    public String leerArchivo(String ruta) {
        try {
            return new String(Files.readAllBytes(Paths.get(ruta)));
        } catch (Exception e) {
            return "[]";
        }
    }

    /**
     * Convierte una cadena JSON en una lista de objetos Audio (Cancion o EpisodioPodcast).
     */
    public List<Audio> parsearAudios(String jsonArray) {
        List<Audio> lista = new ArrayList<>();
        List<String> objetos = separarObjetosDeArreglo(jsonArray);

        for (String obj : objetos) {
            String tipo = extraerString(obj, "tipo");
            String categoriastr = extraerString(obj, "categoria");
            Audio.Clasificacion categoria = Audio.Clasificacion.PUBLICO;
            if (categoriastr.equalsIgnoreCase("MAYOR")) {
                categoria = Audio.Clasificacion.MAYOR;
            }
            if (tipo.equalsIgnoreCase("cancion")) {
                lista.add(new Cancion(
                        extraerString(obj, "id"),
                        extraerString(obj, "titulo"),
                        extraerInt(obj, "duracion"),
                        extraerString(obj, "artista"),
                        extraerString(obj, "album"),
                        extraerString(obj, "genero"),
                        categoria

                ));
            } else if (tipo.equalsIgnoreCase("episodio")) {
                lista.add(new EpisodioPodcast(
                        extraerString(obj, "id"),
                        extraerString(obj, "titulo"),
                        extraerInt(obj, "duracion"),
                        extraerString(obj, "anfitrion"),
                        extraerString(obj, "nombrePodcast"),
                        extraerString(obj, "descripcion"),
                        extraerInt(obj, "numeroEpisodio"),
                        categoria
                ));
            }
        }
        return lista;
    }

    /**
     * Convierte una cadena JSON en una lista de objetos Producto (ArteVisualAlbum o PaqueteTopTen).
     */
    public List<Producto> parsearProductos(String jsonArray) {
        List<Producto> lista = new ArrayList<>();
        List<String> objetos = separarObjetosDeArreglo(jsonArray);

        for (String obj : objetos) {
            String tipo = extraerString(obj, "tipo");
            String categorias = extraerString(obj , "categoria");
            Audio.Clasificacion categoria = Audio.Clasificacion.valueOf(categorias.toUpperCase());
            if (tipo.equalsIgnoreCase("arte_visual")) {
                lista.add(new ArteVisualAlbum(
                        extraerString(obj, "id"),
                        extraerString(obj, "nombre"),
                        extraerDouble(obj, "precio"),
                        extraerString(obj, "descripcion"),
                        extraerString(obj, "albumAsociado"),
                        extraerString(obj, "artista"),
                        extraerString(obj, "formato")
                ));
            } else if (tipo.equalsIgnoreCase("paquete_top_ten")) {
                PaqueteTopTen p = new PaqueteTopTen(
                        extraerString(obj, "id"),
                        extraerString(obj, "nombre"),
                        extraerDouble(obj, "precio"),
                        extraerString(obj, "descripcion"),
                        extraerString(obj, "tematica")
                );
                String arregloIds = extraerBloque(obj, "idsCanciones", '[', ']');
                List<String> idsStr = separarCadenasDeArreglo(arregloIds);
                for (String id : idsStr) {
                    p.agregarCancion(id);
                }
                lista.add(p);
            }
        }
        return lista;
    }

    /**
     * Convierte una cadena JSON en una lista de objetos Usuario con todas sus dependencias.
     */
    public List<Usuario> parsearUsuarios(String jsonArray) {
        List<Usuario> lista = new ArrayList<>();
        List<String> objetos = separarObjetosDeArreglo(jsonArray);

        for (String obj : objetos) {
            String id = extraerString(obj, "id");
            String nombre = extraerString(obj, "nombre");
            String correo = extraerString(obj, "correo");
            String rolStr = extraerString(obj, "rol");
            Usuario.RolUsuario rol = Usuario.RolUsuario.NORMAL;
            if (rolStr.equalsIgnoreCase("ADMINISTRADOR")) {
                rol = Usuario.RolUsuario.ADMINISTRADOR;
            }
            double saldo= extraerDouble(obj ,"saldo");
            boolean controParental = extraerBoolean(obj , "controlParental");
            int edad= extraerInt(obj, "edad");




            String st = extraerBloque(obj, "estadisticas", '{', '}');
            Estadisticas estadisticas = new Estadisticas(
                    extraerLong(st, "tiempoEscuchaSegundos"),
                    extraerInt(st, "cancionesEnBiblioteca"),
                    extraerInt(st, "comprasRealizadas"),
                    extraerInt(st, "reproduccionesTotales")
            );

            String arrCompras = extraerBloque(obj, "historialCompras", '[', ']');
            List<Compra> historial = parsearCompras(arrCompras);

            Biblioteca biblioteca = new Biblioteca();
            String biblioObj = extraerBloque(obj, "biblioteca", '{', '}');
            String arrPlaylists = extraerBloque(biblioObj, "playlists", '[', ']');
            List<String> playlistsTxt = separarObjetosDeArreglo(arrPlaylists);

            for (String plTxt : playlistsTxt) {
                String contenidoTxt = extraerBloque(plTxt, "contenido", '[', ']');
                List<Audio> contenido = parsearAudios(contenidoTxt);
                Playlist playlist = new Playlist(
                        extraerString(plTxt, "id"),
                        extraerString(plTxt, "nombre"),
                        extraerString(plTxt, "aliasPropietario"),
                        contenido
                );
                biblioteca.agregarPlaylist(playlist);
            }

            lista.add(new Usuario(id, nombre, correo, biblioteca, estadisticas, historial,  rol , saldo , controParental ,edad));
        }
        return lista;
    }

    /**
     * Convierte una cadena JSON en una lista de objetos Compra.
     */
    public List<Compra> parsearCompras(String jsonArray) {
        List<Compra> lista = new ArrayList<>();
        List<String> objetos = separarObjetosDeArreglo(jsonArray);
        for (String obj : objetos) {
            lista.add(new Compra(
                    extraerString(obj, "idProducto"),
                    extraerString(obj, "aliasUsuario"),
                    extraerDouble(obj, "montoPagado"),
                    LocalDateTime.parse(extraerString(obj, "fecha"))
            ));
        }
        return lista;
    }

    /**
     * Extrae el valor de texto (String) asociado a una clave en el JSON.
     */
    private String extraerString(String json, String clave) {
        String patron = "\"" + clave + "\":\"";
        int idx = json.indexOf(patron);
        if (idx == -1) return "";
        int inicio = idx + patron.length();
        int fin = json.indexOf("\"", inicio);
        return json.substring(inicio, fin);
    }

    /**
     * Extrae el valor numerico entero (int) asociado a una clave en el JSON.
     */
    private int extraerInt(String json, String clave) {
        return (int) Double.parseDouble(extraerNumero(json, clave));
    }

    /**
     * Extrae el valor decimal (double) asociado a una clave en el JSON.
     */
    private double extraerDouble(String json, String clave) {
        return Double.parseDouble(extraerNumero(json, clave));
    }

    /**
     * Extrae el valor numerico largo (long) asociado a una clave en el JSON.
     */
    private long extraerLong(String json, String clave) {
        return (long) Double.parseDouble(extraerNumero(json, clave));
    }

    /**
     * Localiza un numero en formato crudo dentro del JSON.
     */
    private String extraerNumero(String json, String clave) {
        String patron = "\"" + clave + "\":";
        int idx = json.indexOf(patron);
        if (idx == -1) return "0";
        int inicio = idx + patron.length();
        int fin = inicio;
        while (fin < json.length() && (Character.isDigit(json.charAt(fin)) || json.charAt(fin) == '.' || json.charAt(fin) == '-')) {
            fin++;
        }
        String num = json.substring(inicio, fin);
        return num.isEmpty() ? "0" : num;
    }

    /**
     * Extrae un bloque completo de JSON delimitado por caracteres de apertura y cierre.
     */
    private String extraerBloque(String json, String clave, char apertura, char cierre) {
        String patron = "\"" + clave + "\":";
        int idx = json.indexOf(patron);
        if (idx == -1) return "";
        int inicio = json.indexOf(apertura, idx);
        if (inicio == -1) return "";

        int abiertas = 0;
        int fin = -1;
        boolean comillas = false;
        for (int i = inicio; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '"') comillas = !comillas;
            if (!comillas) {
                if (c == apertura) abiertas++;
                if (c == cierre) abiertas--;
                if (abiertas == 0) {
                    fin = i + 1;
                    break;
                }
            }
        }
        return fin != -1 ? json.substring(inicio, fin) : "";
    }

    /**
     * Separa un arreglo JSON de objetos en una lista de cadenas individuales.
     */
    private List<String> separarObjetosDeArreglo(String jsonArray) {
        List<String> objetos = new ArrayList<>();
        if (jsonArray == null || jsonArray.isEmpty()) return objetos;
        int llavesAbiertas = 0;
        StringBuilder objActual = new StringBuilder();
        boolean comillas = false;

        for (char c : jsonArray.toCharArray()) {
            if (c == '"') comillas = !comillas;
            if (!comillas) {
                if (c == '{') llavesAbiertas++;
                if (llavesAbiertas > 0) objActual.append(c);
                if (c == '}') {
                    llavesAbiertas--;
                    if (llavesAbiertas == 0) {
                        objetos.add(objActual.toString());
                        objActual = new StringBuilder();
                    }
                }
            } else if (llavesAbiertas > 0) {
                objActual.append(c);
            }
        }
        return objetos;
    }

    /**
     * Separa un arreglo JSON de cadenas en una lista de textos.
     */
    private List<String> separarCadenasDeArreglo(String jsonArray) {
        List<String> cadenas = new ArrayList<>();
        if (jsonArray == null || jsonArray.isEmpty()) return cadenas;
        boolean comillas = false;
        StringBuilder actual = new StringBuilder();
        for (char c : jsonArray.toCharArray()) {
            if (c == '"') {
                if (comillas) cadenas.add(actual.toString());
                actual = new StringBuilder();
                comillas = !comillas;
            } else if (comillas) {
                actual.append(c);
            }
        }
        return cadenas;
    }
    /**
     * Busca una clave en un bloque JSON y extrae su valor booleano.
     * Si no encuentra la clave o hay un error, devuelve false por defecto.
     */
    private boolean extraerBoolean(String json, String clave) {
        String patron = "\"" + clave + "\":";
        int inicio = json.indexOf(patron);

        if (inicio == -1) {
            return false;
        }
        inicio += patron.length();
        int finComa = json.indexOf(",", inicio);
        int finLlave = json.indexOf("}", inicio);

        int fin;
        if (finComa != -1 && finLlave != -1) {
            fin = Math.min(finComa, finLlave);
        } else if (finComa != -1) {
            fin = finComa;
        } else if (finLlave != -1) {
            fin = finLlave;
        } else {
            fin = json.length();
        }

        String valorStr = json.substring(inicio, fin).trim();

        valorStr = valorStr.replace("\"", "");
        return Boolean.parseBoolean(valorStr);
    }
}