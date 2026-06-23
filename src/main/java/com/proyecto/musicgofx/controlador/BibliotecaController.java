package com.proyecto.musicgofx.controlador;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import com.proyecto.musicgofx.modelo.entidades.Audio;
import com.proyecto.musicgofx.modelo.entidades.Playlist;
import com.proyecto.musicgofx.modelo.entidades.Usuario;
import com.proyecto.musicgofx.modelo.servicios.GestorPlaylists;
import com.proyecto.musicgofx.modelo.servicios.GestorReproduccion;


public class BibliotecaController {
    @FXML private TextField txtNombrePlaylist;
    @FXML private FlowPane panelPlaylists;

    private Usuario usuarioLogueado;
    private GestorPlaylists gestorPlaylists;
    private GestorReproduccion gestorReproduccion;
    private MainController mainController;
    private Audio audioPendiente;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setGestores(GestorPlaylists gestorp , GestorReproduccion gestror){
        this.gestorPlaylists=gestorp;
        this.gestorReproduccion= gestror;
    }
    public void setUsuarioLogueado(Usuario  usuarioLogueado){
        this.usuarioLogueado =usuarioLogueado;
        cargarPlaylistVisuales();
    }

    public void prepararCreacionConAudio(Audio audio) {
        this.audioPendiente = audio;
        if (txtNombrePlaylist != null && audio != null) {
            txtNombrePlaylist.setPromptText("Guardar: " + audio.getTitulo());
        }
    }
    private void cargarPlaylistVisuales() {
        if (panelPlaylists == null) return;
        panelPlaylists.getChildren().clear(); 
        
        if (usuarioLogueado == null || usuarioLogueado.getBiblioteca() == null || usuarioLogueado.getBiblioteca().getPlaylists() == null) {
            return;
        }
        
        for (Playlist playlist : usuarioLogueado.getBiblioteca().getPlaylists()) {
            
            VBox tarjeta = new VBox();
            tarjeta.setPrefSize(160, 240); 
            tarjeta.setSpacing(10);
            tarjeta.setAlignment(Pos.CENTER);
            tarjeta.setPadding(new Insets(15));
            tarjeta.getStyleClass().add("tarjeta-audio"); 
            
            Label lblTitulo = new Label(playlist.getNombre());
            lblTitulo.getStyleClass().add("label");

            int cantCanciones = (playlist.getContenido() != null) ? playlist.cantidadAudios() : 0;
            Label lblCantidad = new Label(cantCanciones + " canciones");
            lblCantidad.setStyle("-fx-text-fill: #b3b3b3; -fx-font-size: 12px;");

            Button btnPlay = new Button("▶ Reproducir");
            btnPlay.setMaxWidth(Double.MAX_VALUE);
            btnPlay.getStyleClass().add("button");
            btnPlay.setOnAction(event -> {
                if (playlist.getContenido() != null && !playlist.getContenido().isEmpty()) {
                    gestorReproduccion.iniciarColaDeReproduccion(playlist.getContenido(), 0, usuarioLogueado);
                }
            });

            Button btnVerContenido = new Button("ℹ Ver Contenido");
            btnVerContenido.setMaxWidth(Double.MAX_VALUE);
            btnVerContenido.getStyleClass().add("btn-agregar-playlist");
            btnVerContenido.setOnAction(event -> {
                // ====================================================================
                // TODO: CONECTAR CON EL MAIN CONTROLLER EN LA FASE DE NAVEGACIÓN
                // ====================================================================
                // PASO A: Llamar al controlador principal (mainController).
                // PASO B: Solicitarle que cambie la pantalla actual al panel de "Ver Playlist".
                // PASO C: Pasarle como argumento el objeto 'playlist' actual para que
                //         la nueva pantalla sepa qué canciones tiene que dibujar.
                //
                // El diseño conceptual de la línea de código del futuro será algo como:
                // mainController.cambiarAPantallaVerPlaylist(playlist);
                // ====================================================================
                System.out.println("El usuario quiere ver la playlist: " + playlist.getNombre());
            });

            Button btnEliminar = new Button("🗑 Eliminar");
            btnEliminar.setMaxWidth(Double.MAX_VALUE);
            btnEliminar.getStyleClass().add("button");
            btnEliminar.setOnAction(event -> {
                Alert alertaConfirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                alertaConfirmacion.setTitle("Confirmar eliminación");
                alertaConfirmacion.setHeaderText(null);
                alertaConfirmacion.setContentText("¿Estás seguro de que deseas eliminar la playlist '" + playlist.getNombre() + "'? Esta acción no se puede deshacer.");
                String rutaCss = getClass().getResource("/com/proyecto/musicgofx/styles.css").toExternalForm();
                alertaConfirmacion.getDialogPane().getStylesheets().add(rutaCss);
                alertaConfirmacion.getDialogPane().getStyleClass().add("ventana-emergente-oscura");
                java.util.Optional<javafx.scene.control.ButtonType> resultado = alertaConfirmacion.showAndWait();
                if (resultado.isPresent() && resultado.get() == javafx.scene.control.ButtonType.OK) {
                    boolean exito = gestorPlaylists.eliminarPlaylist(usuarioLogueado, playlist.getNombre());
                    if (exito) {
                        Alert alertaExito = new Alert(Alert.AlertType.INFORMATION);
                        alertaExito.setTitle("Eliminada");
                        alertaExito.setHeaderText(null);
                        alertaExito.setContentText("La playlist '" + playlist.getNombre() + "' se ha eliminado correctamente.");
                        alertaExito.getDialogPane().getStylesheets().add(rutaCss);
                        alertaExito.getDialogPane().getStyleClass().add("ventana-emergente-oscura");
                        alertaExito.showAndWait();
                        cargarPlaylistVisuales();
                    } else {
                        Alert alertaError = new Alert(Alert.AlertType.ERROR);
                        alertaError.setTitle("Error");
                        alertaError.setHeaderText(null);
                        alertaError.setContentText("No se pudo eliminar la playlist en este momento.");
                        alertaError.showAndWait();
                    }
                }
            });
            tarjeta.getChildren().addAll(lblTitulo, lblCantidad, btnPlay, btnVerContenido, btnEliminar);
            panelPlaylists.getChildren().add(tarjeta);
        }
    }
    @FXML
    public void crearNuevaPlaylist(ActionEvent event) {
        if (usuarioLogueado ==null) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error de Sesión");
            alerta.setHeaderText(null);
            alerta.setContentText("No se pudo crear la playlist porque no hay un usuario activo en esta sesión.");
            alerta.showAndWait();
            return;
        }
        String nombre = txtNombrePlaylist.getText().trim();

        if (nombre.isEmpty()) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Campo vacío");
            alerta.setContentText("Por favor, ingresa un nombre para la playlist.");
            alerta.showAndWait();
            return;
        }

        gestorPlaylists.crearPlaylist(usuarioLogueado.getNombre(), nombre);
        if (audioPendiente != null) {
            gestorPlaylists.agregarAudioAPlaylist(usuarioLogueado, nombre, audioPendiente);
            audioPendiente = null;
            txtNombrePlaylist.setPromptText("Ej. Para el Gym...");
        }

        txtNombrePlaylist.clear();
        cargarPlaylistVisuales();

        Alert alertaExito = new Alert(Alert.AlertType.INFORMATION);
        alertaExito.setTitle("Éxito");
        alertaExito.setContentText("¡Playlist '" + nombre + "' procesada correctamente!");
        alertaExito.showAndWait();
    }
}
