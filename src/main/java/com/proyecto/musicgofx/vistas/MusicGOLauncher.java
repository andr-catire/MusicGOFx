package vistas;

import javax.swing.*;

import com.proyecto.musicgofx.modelo.persistencia.RepositorioDatos;
import com.proyecto.musicgofx.modelo.servicios.*;

/**
 * ============================================================
 *  MUSICGO - LAUNCHER DE LA APLICACION (Galeria Noir)
 * ============================================================
 *  Punto de entrada principal para la interfaz grafica.
 *  Configura el Look & Feel y lanza la aplicacion.
 */
public class MusicGOLauncher {

    public static void main(String[] args) {
        // Configurar Look & Feel personalizado
        configurarLookAndFeel();

        SwingUtilities.invokeLater(() -> {
            try {
                // Inicializar servicios
                RepositorioDatos repo = new RepositorioDatos();
                GestorUsuarios gu = new GestorUsuarios(repo);
                GestorCatalogo gc = new GestorCatalogo();
                gc.cargarDesdeJson();
                GestorPlaylists gp = new GestorPlaylists(gu, gc);
                GestorReproduccion gr = new GestorReproduccion(gu, gc);
                GestorCompras gcom = new GestorCompras(gc, gu, repo);
                GestorEstadisticas ge = new GestorEstadisticas(gu);

                // Crear y mostrar la aplicacion
                MusicGOApp app = new MusicGOApp(gu, gc, gp, gr, gcom, ge);
                app.setVisible(true);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Error al iniciar MusicGO: " + e.getMessage(),
                        "Error Critico",
                        JOptionPane.ERROR_MESSAGE
                );
                e.printStackTrace();
            }
        });
    }

    private static void configurarLookAndFeel() {
        try {
            // Usar Look & Feel del sistema para mejor integracion
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Configurar colores globales para componentes Swing (Galeria Noir)
            UIManager.put("Panel.background", MusicGOApp.NOIR_BLACK);
            UIManager.put("OptionPane.background", MusicGOApp.NOIR_BLACK);
            UIManager.put("OptionPane.messageForeground", MusicGOApp.NOIR_BEIGE);
            UIManager.put("TextField.background", MusicGOApp.NOIR_DARK);
            UIManager.put("TextField.foreground", MusicGOApp.NOIR_BEIGE);
            UIManager.put("TextField.caretForeground", MusicGOApp.NOIR_BEIGE);
            UIManager.put("PasswordField.background", MusicGOApp.NOIR_DARK);
            UIManager.put("PasswordField.foreground", MusicGOApp.NOIR_BEIGE);
            UIManager.put("ComboBox.background", MusicGOApp.NOIR_DARK);
            UIManager.put("ComboBox.foreground", MusicGOApp.NOIR_BEIGE);
            UIManager.put("List.background", MusicGOApp.NOIR_DARK);
            UIManager.put("List.foreground", MusicGOApp.NOIR_BEIGE);
            UIManager.put("Table.background", MusicGOApp.NOIR_BLACK);
            UIManager.put("Table.foreground", MusicGOApp.NOIR_BEIGE);
            UIManager.put("Table.gridColor", MusicGOApp.NOIR_DARK);
            UIManager.put("TableHeader.background", MusicGOApp.NOIR_DARK);
            UIManager.put("TableHeader.foreground", MusicGOApp.NOIR_BEIGE_MED);
            UIManager.put("ScrollPane.background", MusicGOApp.NOIR_BLACK);
            UIManager.put("Viewport.background", MusicGOApp.NOIR_BLACK);
            UIManager.put("Button.background", MusicGOApp.NOIR_DARK);
            UIManager.put("Button.foreground", MusicGOApp.NOIR_BEIGE);
            UIManager.put("Label.foreground", MusicGOApp.NOIR_BEIGE);

        } catch (Exception e) {
            System.err.println("No se pudo configurar Look & Feel: " + e.getMessage());
        }
    }
}