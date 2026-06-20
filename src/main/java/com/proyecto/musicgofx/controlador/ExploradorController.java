package com.proyecto.musicgofx.controlador;

import com.proyecto.musicgofx.modelo.entidades.Audio;
import com.proyecto.musicgofx.modelo.entidades.Cancion;
import com.proyecto.musicgofx.modelo.entidades.EpisodioPodcast; // IMPORTANTE AÑADIR ESTO
import com.proyecto.musicgofx.modelo.servicios.GestorCatalogo;
import com.proyecto.musicgofx.modelo.servicios.GestorExplorador;
import com.proyecto.musicgofx.modelo.servicios.GestorReproduccion;
import com.proyecto.musicgofx.modelo.entidades.Usuario;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ExploradorController {

    // ════════════════════════════════════════════════════
    // 1. ELEMENTOS DE LA INTERFAZ (FXML)
    // ════════════════════════════════════════════════════
    @FXML private ToggleButton btnFiltrarTodo;
    @FXML private ToggleButton btnFiltrarCanciones;
    @FXML private ToggleButton btnFiltrarPodcast;
    @FXML private ToggleGroup grupoFiltros;
    @FXML private ComboBox<String> menuGeneros;
    @FXML private FlowPane flowPaneAudios;

    // ════════════════════════════════════════════════════
    // 2. SERVICIOS Y VARIABLES DE DATOS
    // ════════════════════════════════════════════════════
    private GestorCatalogo gestorCatalogo;
    private GestorExplorador gestorExplorador;
    private List<Audio> catalogoCompleto;
    private String textoBusquedaActual = "";

    // Variables para el reproductor
    private GestorReproduccion gestorReproduccion;
    private Usuario usuarioActual;

    // ════════════════════════════════════════════════════
    // 3. INICIALIZACIÓN
    // ════════════════════════════════════════════════════
    @FXML
    public void initialize() {
        gestorCatalogo = new GestorCatalogo();
        gestorExplorador = new GestorExplorador();

        gestorCatalogo.cargarDesdeJson();
        catalogoCompleto = gestorCatalogo.getTodosLosAudios();

        menuGeneros.getItems().addAll("Todos los géneros", "Pop", "Rock", "Reggaeton", "Urbano", "Educativo", "Comedia");
        menuGeneros.setValue("Todos los géneros");

        menuGeneros.setOnAction(event -> aplicarFiltros());

        grupoFiltros.selectedToggleProperty().addListener((observable, valorViejo, valorNuevo) -> {
            if (valorNuevo == null) {
                valorViejo.setSelected(true);
            } else {
                aplicarFiltros();
            }
        });

        btnFiltrarTodo.setSelected(true);
        aplicarFiltros();
    }

    // ════════════════════════════════════════════════════
    // 4. MÉTODOS DE COMUNICACIÓN Y LÓGICA VISUAL
    // ════════════════════════════════════════════════════

    public void recibirBusqueda(String textoBusqueda) {
        this.textoBusquedaActual = textoBusqueda;
        aplicarFiltros();
    }

    @FXML
    private void aplicarFiltros() {
        String tipoSeleccionado = "Todos";
        if (grupoFiltros.getSelectedToggle() != null) {
            ToggleButton botonActivo = (ToggleButton) grupoFiltros.getSelectedToggle();
            tipoSeleccionado = botonActivo.getText();
        }

        String generoSeleccionado = menuGeneros.getValue();

        List<Audio> resultados = gestorExplorador.filtrarCatalogo(
                catalogoCompleto,
                textoBusquedaActual,
                tipoSeleccionado,
                generoSeleccionado
        );

        actualizarPanelResultados(resultados);
    }

    private void actualizarPanelResultados(List<Audio> audiosParaMostrar) {
        flowPaneAudios.getChildren().clear();

        for (Audio audio : audiosParaMostrar) {

            VBox tarjeta = new VBox();
            tarjeta.getStyleClass().add("tarjeta-audio");
            tarjeta.setPrefSize(160, 200);
            tarjeta.setAlignment(Pos.CENTER);
            tarjeta.setSpacing(10);
            tarjeta.setStyle("-fx-padding: 15px;");

            Label lblTitulo = new Label(audio.getTitulo());
            lblTitulo.setStyle("-fx-text-fill: #DDB7FF; -fx-font-weight: bold; -fx-font-size: 14px;");

            String autorText = "Desconocido";
            if (audio instanceof Cancion) {
                autorText = ((Cancion) audio).getArtista();
            } else if (audio instanceof EpisodioPodcast) {
                autorText = ((EpisodioPodcast) audio).getAnfitrion();
            }

            Label lblAutor = new Label(autorText);
            lblAutor.setStyle("-fx-text-fill: #b3b3b3; -fx-font-size: 12px;");
            Button btnPlay = new Button("▶ Reproducir");
            btnPlay.getStyleClass().add("button");

            btnPlay.setOnAction(e -> {
                if (gestorReproduccion != null) {
                    int posicion = audiosParaMostrar.indexOf(audio);
                    gestorReproduccion.iniciarColaDeReproduccion(audiosParaMostrar, posicion, usuarioActual);
                    System.out.println("Enviando al reproductor: " + audio.getTitulo());
                } else {
                    System.err.println("Error: El gestor de reproducción es nulo.");
                }
            });

            tarjeta.getChildren().addAll(lblTitulo, lblAutor, btnPlay);
            flowPaneAudios.getChildren().add(tarjeta);
        }
    }

    public void setGestorReproduccion(GestorReproduccion gestor) {
        this.gestorReproduccion = gestor;
    }

    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
    }
}