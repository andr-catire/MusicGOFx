

import com.proyecto.musicgofx.modelo.persistencia.RepositorioDatos;
import com.proyecto.musicgofx.modelo.servicios.*;
import vistas.MusicGOApp;
import javax.swing.SwingUtilities;

/**
 * ============================================================
 *  MUSICGO - PUNTO DE ENTRADA PRINCIPAL
 * ============================================================
 *  Lanza la interfaz grafica "Galeria Noir" de MusicGO.
 *  Estilo: Tocadiscos de vinilo, paleta beige/negro elegante.
 */
public class Main {
    public static void main(String[] args) {

        // ============================================================
        //  INICIALIZACION DE SERVICIOS
        // ============================================================
        GestorCatalogo gestorCatalogo = new GestorCatalogo();
        gestorCatalogo.cargarDesdeJson();

        RepositorioDatos repo = new RepositorioDatos();
        GestorUsuarios gestorUsuarios = new GestorUsuarios(repo);

        GestorPlaylists gestorPlaylists = new GestorPlaylists(gestorUsuarios, gestorCatalogo);
        GestorCompras gestorCompras = new GestorCompras(gestorCatalogo, gestorUsuarios, repo);
        GestorReproduccion gestorReproduccion = new GestorReproduccion(gestorUsuarios, gestorCatalogo);
        GestorEstadisticas gestorEstadisticas = new GestorEstadisticas(gestorUsuarios);

        // ============================================================
        //  LANZAR INTERFAZ GRAFICA - GALERIA NOIR
        // ============================================================
        SwingUtilities.invokeLater(() -> {
            MusicGOApp app = new MusicGOApp(
                    gestorUsuarios,
                    gestorCatalogo,
                    gestorPlaylists,
                    gestorReproduccion,
                    gestorCompras,
                    gestorEstadisticas
            );
            app.setVisible(true);
        });
    }
}