package com.proyecto.musicgofx.controlador;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import com.proyecto.musicgofx.modelo.servicios.GestorCatalogo;
import com.proyecto.musicgofx.modelo.servicios.GestorReproduccion;
import com.proyecto.musicgofx.modelo.servicios.GestorUsuarios;
import com.proyecto.musicgofx.modelo.persistencia.RepositorioDatos;
import com.proyecto.musicgofx.modelo.entidades.Usuario;
import com.proyecto.musicgofx.modelo.entidades.Audio;

import java.util.List;

public class InicioController {

    @FXML
    private FlowPane flowPaneAudios;

    private RepositorioDatos repositorio = new RepositorioDatos();
    private GestorCatalogo catalogo = new GestorCatalogo();
    private GestorUsuarios usuarios = new GestorUsuarios(repositorio);
    private GestorReproduccion reproduccion = new GestorReproduccion(usuarios, catalogo);


    private Usuario usuarioLogueado;

    /**
     * Este método lo llamaremos desde el MainController o LoginController
     * para decirle a esta pantalla quién acaba de iniciar sesión.
     */
    public void setUsuarioLogueado(Usuario usuario) {

        this.usuarioLogueado = usuario;
        cargarTarjetasDeAudio()
;    }
    private void cargarTarjetasDeAudio() {
        catalogo.cargarDesdeJson();
        List<Audio> listaAudios = catalogo.getTodosLosAudios();

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

            Button btnReproducir = new Button("Reproducir");
            btnReproducir.getStyleClass().add("button");

            btnReproducir.setOnAction(event -> {
                if (usuarioLogueado != null) {
                    reproduccion.reproducirAudio(usuarioLogueado.getNombre(), audio.getTitulo());
                } else {
                    System.out.println("Error: Nadie ha iniciado sesión.");
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