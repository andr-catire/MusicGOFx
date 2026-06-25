package com.proyecto.musicgofx.controlador;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.proyecto.musicgofx.modelo.servicios.GestorReproduccion;
import com.proyecto.musicgofx.modelo.servicios.GestorPlaylists;
import com.proyecto.musicgofx.modelo.entidades.Usuario;
import com.proyecto.musicgofx.modelo.entidades.Audio;
import com.proyecto.musicgofx.modelo.entidades.Playlist;

import java.util.ArrayList;
import java.util.List;

public class  InicioController {

    @FXML
    private FlowPane flowPaneAudios;


    private GestorReproduccion gestorReproduccion;
    private GestorPlaylists gestorPlaylists;
    private Usuario usuarioLogueado;
    private MainController mainController;

    public void setUsuarioLogueado(Usuario usuario) {
        this.usuarioLogueado = usuario;
        verificarYRenderizar();
    }

    public void setGestorReproduccion(GestorReproduccion gestor) {
        this.gestorReproduccion = gestor;
        verificarYRenderizar();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setGestorPlaylists(GestorPlaylists gestorPlaylists) {
        this.gestorPlaylists = gestorPlaylists;
    }

    private void verificarYRenderizar() {
        if (this.usuarioLogueado != null && this.gestorReproduccion != null) {
            cargarTarjetasDeAudio();
        }
    }

    // ════════════════════════════════════════════════════
    // LÓGICA DE INTERFAZ
    // ════════════════════════════════════════════════════
    private void cargarTarjetasDeAudio() {
        List<Audio> listaAudios = gestorReproduccion.getTodosLosAudios();
        flowPaneAudios.getChildren().clear();

        for (Audio audio : listaAudios) {
            if (usuarioLogueado.isControlParental() && audio.getCategoria().name().equals("MAYOR")) {
                continue; // Oculta canciones explícitas si hay control parental
            }

            VBox cajaAudio = new VBox();
            cajaAudio.setPrefSize(160, 240);
            cajaAudio.setAlignment(Pos.CENTER);
            cajaAudio.setSpacing(10);
            cajaAudio.setPadding(new Insets(15));
            cajaAudio.getStyleClass().add("tarjeta-audio");

            Label lblPortada = new Label(audio.getTitulo());
            lblPortada.getStyleClass().add("label");

            Button btnReproducir = new Button("▶ Reproducir");
            btnReproducir.setMaxWidth(Double.MAX_VALUE);
            btnReproducir.getStyleClass().add("button");

            Button btnAgregar = new Button("+ Agregar a Playlist");
            btnAgregar.setMaxWidth(Double.MAX_VALUE);
            btnAgregar.getStyleClass().add("btn-agregar-playlist");

            btnAgregar.setOnAction(event -> mostrarDialogoPlaylists(audio));

            btnReproducir.setOnAction(event -> {
                int posicion = listaAudios.indexOf(audio);
                gestorReproduccion.iniciarColaDeReproduccion(listaAudios, posicion, usuarioLogueado);
            });

            cajaAudio.getChildren().addAll(lblPortada, btnReproducir, btnAgregar);
            flowPaneAudios.getChildren().add(cajaAudio);
        }
    }

    private void mostrarDialogoPlaylists(Audio audio) {
        if (usuarioLogueado == null) return;

        List<String> opciones = new ArrayList<>();
        String opcionCrearNueva = "➕ Crear nueva playlist en Biblioteca";
        opciones.add(opcionCrearNueva);

        if (usuarioLogueado.getBiblioteca() != null && usuarioLogueado.getBiblioteca().getPlaylists() != null) {
            for (Playlist p : usuarioLogueado.getBiblioteca().getPlaylists()) {
                opciones.add(p.getNombre());
            }
        }

        ChoiceDialog<String> dialogo = new ChoiceDialog<>(opcionCrearNueva, opciones);
        dialogo.setTitle("Agregar a Playlist");
        dialogo.setHeaderText("Guardar: " + audio.getTitulo());
        dialogo.setContentText("Selecciona una opción:");
        dialogo.setGraphic(null);

        Stage stage = (Stage) dialogo.getDialogPane().getScene().getWindow();
        stage.initStyle(StageStyle.UTILITY);
        try {
            String rutaCss = getClass().getResource("/com/proyecto/musicgofx/styles.css").toExternalForm();
            dialogo.getDialogPane().getStylesheets().add(rutaCss);
            dialogo.getDialogPane().getStyleClass().add("ventana-emergente-oscura");
        } catch (Exception e) {}

        dialogo.showAndWait().ifPresent(seleccion -> {
            if (seleccion.equals(opcionCrearNueva)) {
                if (mainController != null) mainController.irABibliotecaParaNuevaPlaylist(audio);
            } else {
                if (gestorPlaylists != null) {
                    boolean exito = gestorPlaylists.agregarAudioAPlaylist(usuarioLogueado, seleccion, audio);

                    if (exito) {
                        Alert alert = new Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                        alert.setTitle("¡Genial!");
                        alert.setHeaderText(null);
                        alert.setContentText("El audio '" + audio.getTitulo() + "' se guardó en tu playlist '" + seleccion + "'.");

                        try {
                            String rutaCss = getClass().getResource("/com/proyecto/musicgofx/styles.css").toExternalForm();
                            alert.getDialogPane().getStylesheets().add(rutaCss);
                        } catch (Exception e) {}

                        alert.showAndWait();
                    } else {
                        Alert alert = new Alert(javafx.scene.control.Alert.AlertType.WARNING);
                        alert.setTitle("Aviso");
                        alert.setHeaderText(null);
                        alert.setContentText("Esta canción ya está en tu playlist '" + seleccion + "'.");
                        String rutaCss = getClass().getResource("/com/proyecto/musicgofx/styles.css").toExternalForm();
                        alert.getDialogPane().getStylesheets().add(rutaCss);
                        alert.getDialogPane().getStyleClass().add("ventana-emergente-oscura");
                        alert.showAndWait();
                    }
                }
            }
        });
    }

    @FXML
    public void initialize() {
    }
}