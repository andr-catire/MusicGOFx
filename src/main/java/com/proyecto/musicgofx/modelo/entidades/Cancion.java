package com.proyecto.musicgofx.modelo.entidades;

/**
 * Representa una cancion dentro del catalogo de MusicGO.
 *
 * Ademas de los datos comunes a todo {@link Audio} (id, titulo, duracion)
 * guarda al artista y al album al que pertenece.
 *
 * @author Equipo MusicGO
 */
public class Cancion extends Audio {

    private String artista;
    private String album;
    private String genero;

    /**
     * Constructor para RECONSTRUIR una cancion (ej. desde JSON).
     * Se usa cuando ya tenemos un ID asignado.
     */
    public Cancion(String id, String titulo, int duracionSegundos,
                   String artista, String album, String genero , Clasificacion  categoria ) {
        super(id, titulo, duracionSegundos , categoria );
        this.artista = artista;
        this.album = album;
        this.genero = genero;
    }

    /**
     * Constructor para CREAR una cancion nueva.
     * El ID se genera automaticamente a traves de la clase padre.
     */
    public Cancion(String titulo, int duracionSegundos,
                   String artista, String album, String genero , Clasificacion categoria  ) {
        super(titulo, duracionSegundos, categoria);
        this.artista = artista;
        this.album = album;
        this.genero = genero;
    }

    public String getArtista() {
        return artista;
    }

    public String getAlbum() {
        return album;
    }

    public String getGenero() {
        return genero;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    @Override
    public String getTipo() {
        return "cancion";
    }

    /**
     * Simula la reproduccion mostrando los datos por consola.
     */
    @Override
    public void reproducir() {
        System.out.println("------------------------------------------");
        System.out.println(" >> Reproduciendo cancion <<");
        System.out.println("    Titulo  : " + getTitulo());
        System.out.println("    Artista : " + artista);
        System.out.println("    Album   : " + album);
        System.out.println("    Genero  : " + genero);
        System.out.println("    Duracion: " + duracionFormateada());
        System.out.println("------------------------------------------");
    }

    @Override
    public String toString() {
        String idCorto = (getId() != null && getId().length() > 8) ? getId().substring(0, 8) : getId();
        return String.format("[Cancion] %s - %s | %s (%s) [%s]",
                idCorto, getTitulo(), artista, album, duracionFormateada());
    }
}