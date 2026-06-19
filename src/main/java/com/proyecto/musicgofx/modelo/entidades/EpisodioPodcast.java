package com.proyecto.musicgofx.modelo.entidades;

/**
 * Representa un episodio de podcast dentro del catalogo.
 *
 * A diferencia de {@link Cancion}, guarda al anfitrion (host),
 * el nombre del podcast al que pertenece y una descripcion
 * del contenido del episodio.
 *
 * @author Equipo MusicGO
 */
public class EpisodioPodcast extends Audio {

    private String anfitrion;
    private String nombrePodcast;
    private String genero;
    private String descripcion;
    private int numeroEpisodio;

    /**
     * Constructor para RECONSTRUIR un episodio (ej. desde JSON).
     * Mantiene el ID persistido en disco.
     */
    public EpisodioPodcast(String id, String titulo, int duracionSegundos,
                           String anfitrion, String nombrePodcast,
                           String descripcion, int numeroEpisodio, Clasificacion  categoria,  String  genero ) {
        super(id, titulo, duracionSegundos, categoria ,genero);
        this.anfitrion = anfitrion;
        this.nombrePodcast = nombrePodcast;
        this.descripcion = descripcion;
        this.numeroEpisodio = numeroEpisodio;

    }

    /**
     * Constructor para CREAR un episodio nuevo.
     * El ID se genera automaticamente mediante UUID.
     */
    public EpisodioPodcast(String titulo, int duracionSegundos,
                           String anfitrion, String nombrePodcast,
                           String descripcion, int numeroEpisodio, Clasificacion categoria  , String genero ) {
        super(titulo, duracionSegundos, categoria, genero);
        this.anfitrion = anfitrion;
        this.nombrePodcast = nombrePodcast;
        this.descripcion = descripcion;
        this.numeroEpisodio = numeroEpisodio;
    }

    public String getAnfitrion() {
        return anfitrion;
    }

    public String getNombrePodcast() {
        return nombrePodcast;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getNumeroEpisodio() {
        return numeroEpisodio;
    }

    public void setAnfitrion(String anfitrion) {
        this.anfitrion = anfitrion;
    }

    public void setNombrePodcast(String nombrePodcast) {
        this.nombrePodcast = nombrePodcast;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setNumeroEpisodio(int numeroEpisodio) {
        this.numeroEpisodio = numeroEpisodio;
    }

    public void setGenero(String genero){this.genero =genero; }

    public String getGenero(){return genero; }

    @Override
    public String getTipo() {
        return "episodio";
    }

    /**
     * Simula la reproduccion mostrando los datos especificos del podcast.
     */
    @Override
    public void reproducir() {
        System.out.println("------------------------------------------");
        System.out.println(" >> Reproduciendo episodio de podcast <<");
        System.out.println("    Podcast    : " + nombrePodcast);
        System.out.println("    Episodio   : #" + numeroEpisodio + " - " + getTitulo());
        System.out.println("    Genero   : #" +  genero);
        System.out.println("    Anfitrion  : " + anfitrion);
        System.out.println("    Duracion   : " + duracionFormateada());
        System.out.println("    Descripcion: " + (descripcion != null ? descripcion : "Sin descripcion."));
        System.out.println("------------------------------------------");
    }

    @Override
    public String toString() {
        String idCorto = (getId() != null && getId().length() > 8) ? getId().substring(0, 8) : getId();
        return String.format("[Podcast] %s - %s | %s (Ep #%d) [%s]",
                idCorto, getTitulo(), nombrePodcast, numeroEpisodio, duracionFormateada());
    }
}