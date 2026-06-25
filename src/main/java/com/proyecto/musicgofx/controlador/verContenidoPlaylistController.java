package com.proyecto.musicgofx.controlador;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import com.proyecto.musicgofx.modelo.entidades.Audio;
import com.proyecto.musicgofx.modelo.entidades.Cancion;
import com.proyecto.musicgofx.modelo.entidades.Playlist;
import com.proyecto.musicgofx.modelo.entidades.Usuario;
import com.proyecto.musicgofx.modelo.servicios.GestorPlaylists;
import com.proyecto.musicgofx.modelo.servicios.GestorReproduccion;

import java.util.Optional;

public class verContenidoPlaylistController {

    @FXML private ImageView imgPortada;
    @FXML private Label lblTituloPlaylist;
    @FXML private Label lblDuracion;
    @FXML private VBox vboxListaCanciones;

    private MainController mainController;
    private GestorPlaylists gestorPlaylists;
    private GestorReproduccion gestorReproduccion;
    private Usuario usuarioLogueado;
    private Playlist playlistActual;

    public void setDependencias(MainController main, GestorPlaylists gp, GestorReproduccion gr, Usuario user) {
        this.mainController = main;
        this.gestorPlaylists = gp;
        this.gestorReproduccion = gr;
        this.usuarioLogueado = user;
    }

    public void inicializarConPlaylist(Playlist playlist) {
        this.playlistActual = playlist;
        lblTituloPlaylist.setText(playlist.getNombre());
        cargarListaCanciones();
    }

    private void cargarListaCanciones() {
        vboxListaCanciones.getChildren().clear();

        if (playlistActual == null || playlistActual.getContenido() == null || playlistActual.getContenido().isEmpty()) {
            lblDuracion.setText("0 canciones");
            Label lblVacia = new Label("Esta playlist está vacía. ¡Busca música para añadir!");
            lblVacia.getStyleClass().add("label-muted");
            vboxListaCanciones.getChildren().add(lblVacia);
            return;
        }

        lblDuracion.setText(playlistActual.getContenido().size() + " canciones");

        for (Audio audio : playlistActual.getContenido()) {
            HBox fila = new HBox();
            fila.setAlignment(Pos.CENTER_LEFT);
            fila.setSpacing(15);
            fila.setPadding(new Insets(10, 15, 10, 15));
            fila.getStyleClass().add("fila-cancion");

            Button btnPlayFila = new Button("▶");
            btnPlayFila.getStyleClass().add("btn-play-fila");
            btnPlayFila.setOnAction(e -> {
                int indiceCancion = playlistActual.getContenido().indexOf(audio);
                gestorReproduccion.iniciarColaDeReproduccion(playlistActual.getContenido(), indiceCancion, usuarioLogueado);
            });

            VBox boxTextos = new VBox(3);
            HBox.setHgrow(boxTextos, Priority.ALWAYS);

            Label lblTitulo = new Label(audio.getTitulo());
            lblTitulo.getStyleClass().add("titulo-cancion-fila");

            String nombreArtista = (audio instanceof Cancion) ? ((Cancion) audio).getArtista() : "Podcast / Audio";
            Label lblArtista = new Label(nombreArtista);
            lblArtista.getStyleClass().add("artista-cancion-fila");

            boxTextos.getChildren().addAll(lblTitulo, lblArtista);

            Button btnRemover = new Button("✕");
            btnRemover.getStyleClass().add("btn-eliminar-fila");
            btnRemover.setOnAction(e -> {
                Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
                alerta.setTitle("Remover Canción");
                alerta.setHeaderText(null);
                alerta.setContentText("¿Estás seguro de que deseas remover '" + audio.getTitulo() + "' de la playlist?");
                aplicarEstiloOscuro(alerta);

                Optional<ButtonType> resultado = alerta.showAndWait();
                if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                    playlistActual.getContenido().remove(audio);
                    cargarListaCanciones();
                }
            });

            fila.getChildren().addAll(btnPlayFila, boxTextos, btnRemover);
            vboxListaCanciones.getChildren().add(fila);
        }
    }

    @FXML
    public void volverABiblioteca() {
        if (mainController != null) {
            mainController.cargarVista("Biblioteca.fxml");
        }
    }

    @FXML
    public void reproducirPlaylist() {
        if (playlistActual != null && !playlistActual.getContenido().isEmpty()) {
            gestorReproduccion.iniciarColaDeReproduccion(playlistActual.getContenido(), 0, usuarioLogueado);
        }
    }

    @FXML
    public void agregarCancion() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Añadir Canción");
        dialog.setHeaderText(null);
        dialog.setContentText("Ingresa el Título o ID exacto de la canción:");
        try {
            String rutaCss = getClass().getResource("/com/proyecto/musicgofx/styles.css").toExternalForm();
            dialog.getDialogPane().getStylesheets().add(rutaCss);
            dialog.getDialogPane().getStyleClass().add("ventana-emergente-oscura");
        } catch (Exception e) {}

        Optional<String> resultado = dialog.showAndWait();
        if (resultado.isPresent() && !resultado.get().trim().isEmpty()) {
            String busqueda = resultado.get().trim().toLowerCase();
            Audio audioEncontrado = null;
            if (gestorReproduccion != null && gestorReproduccion.getTodosLosAudios() != null) {
                for (Audio audio : gestorReproduccion.getTodosLosAudios()) {
                    if (audio.getId().toLowerCase().equals(busqueda) || audio.getTitulo().toLowerCase().contains(busqueda)) {
                        audioEncontrado = audio;
                        break;
                    }
                }
            }
            if (audioEncontrado != null) {
                if (!playlistActual.getContenido().contains(audioEncontrado)) {
                    gestorPlaylists.agregarAudioAPlaylist(usuarioLogueado, playlistActual.getNombre(), audioEncontrado);
                    cargarListaCanciones(); // Recarga la vista al instante
                } else {
                    mostrarAlertaInformativa("Información", "La canción ya se encuentra en esta playlist.", Alert.AlertType.INFORMATION);
                }
            } else {
                mostrarAlertaInformativa("No Encontrado", "No se encontró ningún audio con el ID o título: '" + resultado.get() + "'", Alert.AlertType.WARNING);
            }
        }
    }

    @FXML
    public void eliminarPlaylist() {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Eliminar Playlist");
        alerta.setHeaderText(null);
        alerta.setContentText("¿Estás seguro de que deseas eliminar permanentemente la playlist '" + playlistActual.getNombre() + "'?");
        aplicarEstiloOscuro(alerta);

        Optional<ButtonType> resultado = alerta.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            boolean exito = gestorPlaylists.eliminarPlaylist(usuarioLogueado, playlistActual.getNombre());
            if (exito) {
                volverABiblioteca();
            }
        }
    }


    private void mostrarAlertaInformativa(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        aplicarEstiloOscuro(alerta);
        alerta.showAndWait();
    }

    private void aplicarEstiloOscuro(Alert alerta) {
        try {
            String rutaCss = getClass().getResource("/com/proyecto/musicgofx/styles.css").toExternalForm();
            alerta.getDialogPane().getStylesheets().add(rutaCss);
            alerta.getDialogPane().getStyleClass().add("ventana-emergente-oscura");
        } catch (Exception e) {}
    }
}