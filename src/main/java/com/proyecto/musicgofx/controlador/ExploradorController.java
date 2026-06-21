package com.proyecto.musicgofx.controlador;

import com.proyecto.musicgofx.modelo.entidades.Audio;
import com.proyecto.musicgofx.modelo.entidades.Cancion;
import com.proyecto.musicgofx.modelo.entidades.EpisodioPodcast;
import com.proyecto.musicgofx.modelo.entidades.Playlist;
import com.proyecto.musicgofx.modelo.entidades.Usuario;
import com.proyecto.musicgofx.modelo.servicios.GestorExplorador;
import com.proyecto.musicgofx.modelo.servicios.GestorReproduccion;
import com.proyecto.musicgofx.modelo.servicios.GestorPlaylists;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ExploradorController {

    @FXML
    private ToggleButton btnFiltrarTodo;
    @FXML
    private ToggleButton btnFiltrarCanciones;
    @FXML
    private ToggleButton btnFiltrarPodcast;
    @FXML
    private ToggleGroup grupoFiltros;
    @FXML
    private ComboBox<String> cmbGeneros;
    @FXML
    private TextField txtBusquedaRapida;
    @FXML
    private FlowPane flowPaneAudios;

    private GestorReproduccion gestorReproduccion;
    private GestorExplorador gestorExplorador;
    private GestorPlaylists gestorPlaylists;
    private Usuario usuarioActual;
    private MainController mainController;
    private String textoBusquedaGlobal = "";

    public void setGestorReproduccion(GestorReproduccion gestor) {
        this.gestorReproduccion = gestor;
        actualizarPanelResultados();
    }

    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setGestorPlaylists(GestorPlaylists gestorPlaylists) {
        this.gestorPlaylists = gestorPlaylists;
    }

    public void setGestorExplorador(GestorExplorador gestorExplorador) {
        this.gestorExplorador = gestorExplorador;
        actualizarPanelResultados();
    }

    @FXML
    public void initialize() {
        configurarFiltros();
    }

    private void configurarFiltros() {
        if (btnFiltrarTodo != null) btnFiltrarTodo.setSelected(true);
        if (cmbGeneros != null) {
            cmbGeneros.getItems().addAll("Todos", "Rock", "Pop", "Reggaeton", "Jazz", "Tecnologia", "Entrevistas");
            cmbGeneros.setValue("Todos");
            cmbGeneros.setOnAction(e -> actualizarPanelResultados());
        }
        if (grupoFiltros != null) {
            grupoFiltros.selectedToggleProperty().addListener((obs, oldVal, newVal) -> actualizarPanelResultados());
        }
        if (txtBusquedaRapida != null) {
            txtBusquedaRapida.textProperty().addListener((obs, oldVal, newVal) -> actualizarPanelResultados());
        }
    }

    public void recibirBusqueda(String textoBusqueda) {
        System.out.println(" > Explorador ha recibido la palabra desde el Main: " + textoBusqueda);
        this.textoBusquedaGlobal = textoBusqueda;
        javafx.application.Platform.runLater(this::actualizarPanelResultados);
    }

    private void actualizarPanelResultados() {
        if (gestorExplorador == null || gestorReproduccion == null) return;
        String tipoSeleccionado = "Todos";
        if (grupoFiltros != null && grupoFiltros.getSelectedToggle() != null) {
            tipoSeleccionado = ((ToggleButton) grupoFiltros.getSelectedToggle()).getText();
        }
        String generoSeleccionado = (cmbGeneros != null && cmbGeneros.getValue() != null) ? cmbGeneros.getValue() : "Todos";
        String textoBusqueda = this.textoBusquedaGlobal;
        List<Audio> catalogoBase = gestorReproduccion.getTodosLosAudios();
        List<Audio> resultados = gestorExplorador.filtrarCatalogo(catalogoBase, textoBusqueda, tipoSeleccionado, generoSeleccionado);
        renderizarResultados(resultados);
    }

    private void renderizarResultados(List<Audio> audiosParaMostrar) {
        if (flowPaneAudios == null) return;
        flowPaneAudios.getChildren().clear();

        for (Audio audio : audiosParaMostrar) {
            if (usuarioActual != null && usuarioActual.isControlParental() && audio.getCategoria().name().equals("MAYOR")) {
                continue;
            }

            VBox tarjeta = new VBox();
            tarjeta.setPrefSize(160, 240);
            tarjeta.setAlignment(Pos.CENTER);
            tarjeta.setSpacing(10);
            tarjeta.setPadding(new Insets(15));
            tarjeta.getStyleClass().add("tarjeta-audio");

            Label lblTitulo = new Label(audio.getTitulo());
            lblTitulo.getStyleClass().add("label");

            String autorText = (audio instanceof Cancion) ? ((Cancion) audio).getArtista() : ((EpisodioPodcast) audio).getAnfitrion();
            Label lblAutor = new Label(autorText);
            lblAutor.setStyle("-fx-text-fill: #b3b3b3; -fx-font-size: 12px;");

            Button btnPlay = new Button("▶ Reproducir");
            btnPlay.setMaxWidth(Double.MAX_VALUE);
            btnPlay.getStyleClass().add("button");
            btnPlay.setOnAction(e -> {
                int posicion = audiosParaMostrar.indexOf(audio);
                gestorReproduccion.iniciarColaDeReproduccion(audiosParaMostrar, posicion, usuarioActual);
            });

            Button btnAgregar = new Button("+ Agregar a Playlist");
            btnAgregar.setMaxWidth(Double.MAX_VALUE);
            btnAgregar.getStyleClass().add("btn-agregar-playlist");
            btnAgregar.setOnAction(e -> mostrarDialogoPlaylists(audio));

            tarjeta.getChildren().addAll(lblTitulo, lblAutor, btnPlay, btnAgregar);
            flowPaneAudios.getChildren().add(tarjeta);
        }
    }

    private void mostrarDialogoPlaylists(Audio audio) {
        if (usuarioActual == null) return;

        List<String> opciones = new ArrayList<>();
        String opcionCrearNueva = "➕ Crear nueva playlist en Biblioteca";
        opciones.add(opcionCrearNueva);

        if (usuarioActual.getBiblioteca() != null && usuarioActual.getBiblioteca().getPlaylists() != null) {
            for (Playlist p : usuarioActual.getBiblioteca().getPlaylists()) {
                opciones.add(p.getNombre());
            }
        }

        javafx.scene.control.ChoiceDialog<String> dialogo = new javafx.scene.control.ChoiceDialog<>(opcionCrearNueva, opciones);
        dialogo.setTitle("Agregar a Playlist");
        dialogo.setHeaderText("Guardar: " + audio.getTitulo());
        dialogo.setContentText("Selecciona una opción:");
        dialogo.setGraphic(null);

        try {
            String rutaCss = getClass().getResource("/com/proyecto/musicgofx/styles.css").toExternalForm();
            dialogo.getDialogPane().getStylesheets().add(rutaCss);
            dialogo.getDialogPane().getStyleClass().add("ventana-emergente-oscura");
        } catch (Exception e) {
        }

        dialogo.showAndWait().ifPresent(seleccion -> {
            if (seleccion.equals(opcionCrearNueva)) {
                if (mainController != null) mainController.irABibliotecaParaNuevaPlaylist(audio);
            } else {
                if (gestorPlaylists != null){
                    boolean exito = gestorPlaylists.agregarAudioAPlaylist(usuarioActual, seleccion, audio);
                    if (exito) {
                        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                        alert.setTitle("¡Genial!");
                        alert.setHeaderText(null);
                        alert.setContentText("El audio '" + audio.getTitulo() + "' se guardó en tu playlist '" + seleccion + "'.");

                        try {
                            String rutaCss = getClass().getResource("/com/proyecto/musicgofx/styles.css").toExternalForm();
                            alert.getDialogPane().getStylesheets().add(rutaCss);
                        } catch (Exception e) {
                        }

                        alert.showAndWait();
                    } else {
                        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                        alert.setTitle("Aviso");
                        alert.setHeaderText("Cancion ya en Playlist ");
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
}