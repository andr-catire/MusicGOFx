package com.proyecto.musicgofx.modelo.entidades;

import com.proyecto.musicgofx.interfaces.Identificable;
import com.proyecto.musicgofx.interfaces.Reproducible;
import util.GeneradorId;

/**
 * Clase abstracta que representa cualquier contenido de audio
 * disponible en la plataforma MusicGO.
 *
 * Implementa la logica de identificadores (ID) para asegurar que cada
 * pieza de contenido sea rastreable, ya sea nueva o cargada desde disco.
 *
 * @author Equipo MusicGO
 */
public abstract class Audio implements Reproducible, Identificable {

    protected String id;
    protected String titulo;
    protected int duracionSegundos;
    private Clasificacion  categoria;
    public  enum Clasificacion {
        PUBLICO , MAYOR
    }

    /**
     * Constructor para RECONSTRUIR un audio existente (ej. desde JSON).
     * Se usa cuando ya conocemos el ID del objeto.
     *
     * @param id identificador unico persistido
     * @param titulo titulo del audio
     * @param duracionSegundos duracion en segundos
     */
    protected Audio(String id, String titulo, int duracionSegundos, Clasificacion categoria ) {
        this.id = (id == null || id.isBlank()) ? GeneradorId.generarId("AUD") : id;
        this.titulo = titulo;
        setDuracionSegundos(duracionSegundos);
        this.categoria = (categoria != null) ? categoria : Clasificacion.PUBLICO;
    }

    /**
     * Constructor para CREAR un audio nuevo.
     * Genera automaticamente un identificador unico corto con prefijo.
     *
     * @param titulo titulo del audio
     * @param duracionSegundos duracion en segundos
     */
    protected Audio(String titulo, int duracionSegundos, Clasificacion  categoria) {
        this(GeneradorId.generarId("AUD"), titulo, duracionSegundos , categoria  );
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTitulo() {
        return titulo;
    }

    @Override
    public int getDuracionSegundos() {
        return duracionSegundos;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setCategoria(Clasificacion  categoria){this.categoria  = categoria;}

    public Clasificacion  getCategoria(){return categoria; }

    /**
     * Actualiza la duracion validando que sea un valor positivo.
     * @param duracionSegundos tiempo en segundos
     */
    public void setDuracionSegundos(int duracionSegundos) {
        if (duracionSegundos > 0) {
            this.duracionSegundos = duracionSegundos;
        }
    }

    /**
     * Formatea la duracion en mm:ss para mostrarla en la interfaz de consola.
     * @return string con formato de tiempo
     */
    public String duracionFormateada() {
        int min = duracionSegundos / 60;
        int seg = duracionSegundos % 60;
        return String.format("%02d:%02d", min, seg);
    }

    /**
     * Metodo abstracto que obliga a las subclases (Cancion, Episodio)
     * a definir su propia logica de salida por pantalla.
     */
    @Override
    public abstract void reproducir();

    /**
     * Identificador de tipo para procesos de serializacion y filtrado.
     * @return tipo de audio ("cancion" o "episodio")
     */
    public abstract String getTipo();

    @Override
    public String toString() {
        return String.format("[%s] %s (%s)",
                id, titulo, duracionFormateada());
    }
}