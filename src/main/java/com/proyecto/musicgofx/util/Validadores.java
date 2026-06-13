package com.proyecto.musicgofx.util;

import java.util.regex.Pattern;

public class Validadores {

    private static final String GMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    /**
     * Valida que un correo electrónico tenga el formato correcto de Gmail.
     *
     * @param correo Cadena de texto con el correo a evaluar.
     * @return true si cumple con el patrón de Gmail, false en caso contrario.
     */
    public static boolean esGmailValido(String correo) {
        if (correo == null) return false;
        return Pattern.matches(GMAIL_REGEX, correo);
    }

    /**
     * Verifica que una cadena de texto no sea nula ni esté vacía.
     *
     * @param texto Cadena de texto a evaluar.
     * @return true si la cadena contiene al menos un carácter que no sea espacio en blanco.
     */
    public static boolean esTextoValido(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    /**
     * Comprueba si un número entero se encuentra dentro de un rango específico (inclusivo).
     *
     * @param numero Valor entero a evaluar.
     * @param min Límite inferior permitido.
     * @param max Límite superior permitido.
     * @return true si el número está entre min y max, false en caso contrario.
     */
    public static boolean estaEnRango(int numero, int min, int max) {
        return numero >= min && numero <= max;
    }
}