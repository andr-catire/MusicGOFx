package com.proyecto.musicgofx.controlador;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import com.proyecto.musicgofx.modelo.servicios.GestorReproduccion;
import com.proyecto.musicgofx.modelo.entidades.Usuario;
import com.proyecto.musicgofx.modelo.entidades.Audio;

import java.util.ArrayList;
import java.util.List;

public class InicioController {

    @FXML
    private FlowPane flowPaneAudios;

    private GestorReproduccion gestorReproduccion;
    private Usuario usuarioLogueado;

    /**
     * Recibe el usuario desde el MainController
     */
    public void setUsuarioLogueado(Usuario usuario) {
        this.usuarioLogueado = usuario;
        if (this.gestorReproduccion != null) {
            cargarTarjetasDeAudio();
        }
    }

    /**
     * Recibe el Gestor de Reproducción desde el MainController
     */
    public void setGestorReproduccion(GestorReproduccion gestor) {
        this.gestorReproduccion = gestor;
        if (this.usuarioLogueado != null) {
            cargarTarjetasDeAudio();
        }
    }

    private void cargarTarjetasDeAudio() {

        List<Audio> listaAudios = gestorReproduccion.getTodosLosAudios();

        flowPaneAudios.getChildren().clear();

        for (Audio audio : listaAudios) {

            if (usuarioLogueado.isControlParental() && audio.getCategoria().name().equals("MAYOR")) {
                continue;
            }

            VBox cajaAudio = new VBox();
            cajaAudio.setPrefSize(160, 200);
            cajaAudio.setAlignment(Pos.CENTER);
            cajaAudio.setSpacing(10);
            cajaAudio.setPadding(new Insets(15));
            cajaAudio.getStyleClass().add("tarjeta-audio");

            Label lblPortada = new Label();
            lblPortada.setText(audio.getTitulo());

            Button btnReproducir = new Button("▶ Reproducir");
            btnReproducir.getStyleClass().add("button");

            btnReproducir.setOnAction(event -> {
                if (usuarioLogueado != null && gestorReproduccion != null) {
                    int posicion = listaAudios.indexOf(audio);
                    gestorReproduccion.iniciarColaDeReproduccion(listaAudios, posicion, usuarioLogueado);
                } else {
                    System.out.println("Error: Faltan datos para reproducir.");
                }
            });

            cajaAudio.getChildren().addAll(lblPortada, btnReproducir);
            flowPaneAudios.getChildren().add(cajaAudio);
        }
    }

    @FXML
    public void initialize() {
    }
}