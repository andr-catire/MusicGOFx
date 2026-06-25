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
    @FXML private TextField txtBuscarPlaylist;
    @FXML private FlowPane flowPanePlaylists;

    private Usuario usuarioLogueado;
    private GestorPlaylists gestorPlaylists;
    private GestorReproduccion gestorReproduccion;
    private MainController mainController;
    private Audio audioPendiente;

    @FXML
    public void initialize() {

        if (txtBuscarPlaylist != null) {
            txtBuscarPlaylist.textProperty().addListener((observable, oldValue, newValue) -> {
                cargarPlaylistVisuales();
            });
        }
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setGestores(GestorPlaylists gestorp , GestorReproduccion gestror){
        this.gestorPlaylists = gestorp;
        this.gestorReproduccion = gestror;
    }

    public void setUsuarioLogueado(Usuario usuarioLogueado){
        this.usuarioLogueado = usuarioLogueado;
        cargarPlaylistVisuales();
    }

    public void prepararCreacionConAudio(Audio audio) {
        this.audioPendiente = audio;
        if (txtNombrePlaylist != null && audio != null) {
            txtNombrePlaylist.setPromptText("Guardar: " + audio.getTitulo());
        }
    }

    private void cargarPlaylistVisuales() {
        if (flowPanePlaylists == null) return;
        flowPanePlaylists.getChildren().clear();

        if (usuarioLogueado == null || usuarioLogueado.getBiblioteca() == null || usuarioLogueado.getBiblioteca().getPlaylists() == null) {
            return;
        }

        String filtro = (txtBuscarPlaylist != null && txtBuscarPlaylist.getText() != null)
                ? txtBuscarPlaylist.getText().toLowerCase().trim()
                : "";

        for (Playlist playlist : usuarioLogueado.getBiblioteca().getPlaylists()) {


            if (!filtro.isEmpty() && !playlist.getNombre().toLowerCase().contains(filtro)) {
                continue;
            }

            VBox tarjeta = new VBox();
            tarjeta.setPrefSize(160, 220);
            tarjeta.setSpacing(10);
            tarjeta.setAlignment(Pos.CENTER);
            tarjeta.setPadding(new Insets(15));
            tarjeta.getStyleClass().add("tarjeta-audio");


            tarjeta.setOnMouseClicked(event -> {
                if(mainController != null){
                    mainController.abrirVistaPlaylist(playlist);
                }
            });

            Label lblTitulo = new Label(playlist.getNombre());
            lblTitulo.getStyleClass().add("label");
            lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            int cantCanciones = (playlist.getContenido() != null) ? playlist.cantidadAudios() : 0;
            Label lblCantidad = new Label(cantCanciones + " canciones");
            lblCantidad.getStyleClass().add("label-muted");

            Button btnPlay = new Button("▶ Reproducir");
            btnPlay.setMaxWidth(Double.MAX_VALUE);
            btnPlay.getStyleClass().add("button");
            btnPlay.setOnAction(event -> {
                event.consume();
                if (playlist.getContenido() != null && !playlist.getContenido().isEmpty()) {
                    gestorReproduccion.iniciarColaDeReproduccion(playlist.getContenido(), 0, usuarioLogueado);
                }
            });

            Button btnEliminar = new Button(" Eliminar");
            btnEliminar.setMaxWidth(Double.MAX_VALUE);
            btnEliminar.getStyleClass().addAll("button", "secundario");
            btnEliminar.setOnAction(event -> {
                event.consume();
                Alert alertaConfirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                alertaConfirmacion.setTitle("Confirmar eliminación");
                alertaConfirmacion.setHeaderText(null);
                alertaConfirmacion.setContentText("¿Estás seguro de que deseas eliminar la playlist '" + playlist.getNombre() + "'?");

                try {
                    String rutaCss = getClass().getResource("/com/proyecto/musicgofx/styles.css").toExternalForm();
                    alertaConfirmacion.getDialogPane().getStylesheets().add(rutaCss);
                } catch(Exception e) {}
                alertaConfirmacion.getDialogPane().getStyleClass().add("ventana-emergente-oscura");

                java.util.Optional<javafx.scene.control.ButtonType> resultado = alertaConfirmacion.showAndWait();
                if (resultado.isPresent() && resultado.get() == javafx.scene.control.ButtonType.OK) {
                    boolean exito = gestorPlaylists.eliminarPlaylist(usuarioLogueado, playlist.getNombre());
                    if (exito) {
                        cargarPlaylistVisuales();
                    }
                }
            });

            tarjeta.getChildren().addAll(lblTitulo, lblCantidad, btnPlay, btnEliminar);
            flowPanePlaylists.getChildren().add(tarjeta);
        }
    }

    @FXML
    public void crearNuevaPlaylist(ActionEvent event) {
        if (usuarioLogueado == null) return;
        String nombre = txtNombrePlaylist.getText().trim();
        if (nombre.isEmpty()) return;

        gestorPlaylists.crearPlaylist(usuarioLogueado.getNombre(), nombre);
        if (audioPendiente != null) {
            gestorPlaylists.agregarAudioAPlaylist(usuarioLogueado, nombre, audioPendiente);
            audioPendiente = null;
            txtNombrePlaylist.setPromptText("Ej. Para el Gym...");
        }

        txtNombrePlaylist.clear();
        cargarPlaylistVisuales();
    }
}