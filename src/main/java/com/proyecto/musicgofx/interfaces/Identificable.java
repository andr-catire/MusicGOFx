package com.proyecto.musicgofx.interfaces;

/**
 * Contrato basico para cualquier entidad del dominio que necesite
 * identificarse de forma univoca dentro del sistema.
 *
 * Lo uso para no andar repitiendo getId() en cada clase y para
 * poder buscar elementos de manera generica dentro de las colecciones.
 *
 * @author Equipo MusicGO
 */
public interface Identificable {

    /**
     * Devuelve el identificador unico del objeto.
     * @return id como String (suele ser un codigo tipo "C001", "U002", etc.)
     */
    String getId();
}
