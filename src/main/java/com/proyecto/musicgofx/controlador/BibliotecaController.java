package com.proyecto.musicgofx.controlador;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import com.proyecto.musicgofx.modelo.entidades.Audio;
import com.proyecto.musicgofx.modelo.entidades.Playlist;
import com.proyecto.musicgofx.modelo.entidades.Usuario;
import com.proyecto.musicgofx.modelo.entidades.Producto;
import com.proyecto.musicgofx.modelo.entidades.ArteVisualAlbum;
import com.proyecto.musicgofx.modelo.entidades.PaqueteTopTen;
import com.proyecto.musicgofx.modelo.entidades.Compra;
import com.proyecto.musicgofx.modelo.servicios.GestorPlaylists;
import com.proyecto.musicgofx.modelo.servicios.GestorReproduccion;

import java.util.Optional;

/**
 * Controlador para la vista de la Biblioteca.
 * Administra la visualización de Playlists, Paquetes Top Ten y Arte Visual usando estilos CSS.
 */
public class BibliotecaController {

    @FXML private TextField txtNombrePlaylist;
    @FXML private TextField txtBuscarPlaylist;
    @FXML private FlowPane flowPanePlaylists;
    @FXML private FlowPane flowPaneProductos; // Recuperado para que se vean los productos

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

    public void setGestores(GestorPlaylists gestorPlaylists, GestorReproduccion gestorReproduccion) {
        this.gestorPlaylists = gestorPlaylists;
        this.gestorReproduccion = gestorReproduccion;
    }

    public void setUsuarioLogueado(Usuario usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
        cargarPlaylistVisuales();
    }

    public void prepararCreacionConAudio(Audio audioPendiente) {
        this.audioPendiente = audioPendiente;
        if (txtNombrePlaylist != null && audioPendiente != null) {
            txtNombrePlaylist.setPromptText("Guardando: " + audioPendiente.getTitulo());
        }
    }

    /**
     * Limpia y renderiza las tarjetas de Playlists y Productos comprados.
     */
    public void cargarPlaylistVisuales() {
        if (flowPanePlaylists != null) flowPanePlaylists.getChildren().clear();
        if (flowPaneProductos != null) flowPaneProductos.getChildren().clear();

        if (usuarioLogueado == null) return;

        String filtro = txtBuscarPlaylist != null ? txtBuscarPlaylist.getText().toLowerCase().trim() : "";

        if (usuarioLogueado.getBiblioteca() != null && usuarioLogueado.getBiblioteca().getPlaylists() != null) {
            for (Playlist playlist : usuarioLogueado.getBiblioteca().getPlaylists()) {
                if (!filtro.isEmpty() && !playlist.getNombre().toLowerCase().contains(filtro)) continue;

                VBox tarjeta = crearTarjetaPlaylist(playlist);
                if (flowPanePlaylists != null) flowPanePlaylists.getChildren().add(tarjeta);
            }
        }


        if (usuarioLogueado.getHistorialCompras() != null) {
            for (Compra compra : usuarioLogueado.getHistorialCompras()) {
                String idProd = compra.getIdProducto();
                Producto prod = null;

                if (mainController != null) {
                    prod = mainController.buscarProductoPorIdGlobal(idProd);
                }

                if (prod == null) continue;

                if (!filtro.isEmpty() && !prod.getNombre().toLowerCase().contains(filtro)) continue;

                if (prod instanceof PaqueteTopTen) {
                    VBox tarjeta = crearTarjetaPaqueteComoPlaylist((PaqueteTopTen) prod);
                    if (flowPanePlaylists != null) flowPanePlaylists.getChildren().add(tarjeta);
                } else {
                    VBox tarjeta = crearTarjetaProductoComun(prod);
                    if (flowPaneProductos != null) flowPaneProductos.getChildren().add(tarjeta);
                }
            }
        }
    }

    /**
     * Crea tarjeta visual para Playlists usando clases de styles.css
     */
    private VBox crearTarjetaPlaylist(Playlist playlist) {
        VBox tarjeta = new VBox(10);
        tarjeta.setPrefSize(160, 220);
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setPadding(new Insets(15));
        tarjeta.getStyleClass().add("tarjeta-audio"); // Uso correcto de styles.css

        tarjeta.setOnMouseClicked(event -> {
            if (mainController != null) mainController.abrirVistaPlaylist(playlist);
        });

        Label lblTitulo = new Label(playlist.getNombre());
        lblTitulo.getStyleClass().add("label");
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        int cantidad = playlist.cantidadAudios();
        Label lblCantidad = new Label(cantidad + " canciones");
        lblCantidad.getStyleClass().add("label-muted");

        Button btnPlay = new Button("▶ Reproducir");
        btnPlay.setMaxWidth(Double.MAX_VALUE);
        btnPlay.getStyleClass().add("button");
        btnPlay.setOnAction(event -> {
            event.consume();
            if (gestorReproduccion != null && playlist.getContenido() != null && !playlist.getContenido().isEmpty()) {
                gestorReproduccion.iniciarColaDeReproduccion(playlist.getContenido(), 0, usuarioLogueado);
            } else {
                mostrarAlerta("Biblioteca", "La playlist seleccionada está vacía.", Alert.AlertType.INFORMATION);
            }
        });

        Button btnEliminar = new Button(" Eliminar");
        btnEliminar.setMaxWidth(Double.MAX_VALUE);
        btnEliminar.getStyleClass().addAll("button", "secundario");
        btnEliminar.setOnAction(event -> {
            event.consume();
            Alert alertaConfirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            alertaConfirmacion.setTitle("Eliminar Playlist");
            alertaConfirmacion.setHeaderText(null);
            alertaConfirmacion.setContentText("¿Estás seguro de que deseas eliminar permanentemente la playlist '" + playlist.getNombre() + "'?");
            aplicarEstiloOscuro(alertaConfirmacion);

            Optional<ButtonType> resultado = alertaConfirmacion.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                if (gestorPlaylists != null) {
                    boolean exito = gestorPlaylists.eliminarPlaylist(usuarioLogueado, playlist.getNombre());
                    if (exito) cargarPlaylistVisuales();
                }
            }
        });

        tarjeta.getChildren().addAll(lblTitulo, lblCantidad, btnPlay, btnEliminar);
        return tarjeta;
    }

    /**
     * Crea tarjeta visual para Paquetes Top Ten usando clases de styles.css
     */
    private VBox crearTarjetaPaqueteComoPlaylist(PaqueteTopTen paquete) {
        Playlist playlistTemporal = new Playlist(paquete.getNombre(), usuarioLogueado.getNombre());

        if (paquete.getIdsCanciones() != null && mainController != null) {
            for (String idCan : paquete.getIdsCanciones()) {
                Audio audio = mainController.buscarAudioPorIdGlobal(idCan);
                if (audio != null) playlistTemporal.agregarAudio(audio);
            }
        }

        VBox tarjeta = new VBox(10);
        tarjeta.setPrefSize(160, 220);
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setPadding(new Insets(15));
        tarjeta.getStyleClass().add("tarjeta-audio");

        tarjeta.setOnMouseClicked(event -> {
            if(mainController != null) mainController.abrirVistaPlaylist(playlistTemporal);
        });

        Label lblTitulo = new Label(playlistTemporal.getNombre());
        lblTitulo.getStyleClass().add("label");
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        int cantCanciones = playlistTemporal.cantidadAudios();
        Label lblCantidad = new Label(cantCanciones + " canciones (Paquete)");
        lblCantidad.getStyleClass().add("label-muted");

        Button btnPlay = new Button("▶ Reproducir");
        btnPlay.setMaxWidth(Double.MAX_VALUE);
        btnPlay.getStyleClass().add("button");

        btnPlay.setOnAction(event -> {
            event.consume();
            if (playlistTemporal.getContenido() != null && !playlistTemporal.getContenido().isEmpty()) {
                gestorReproduccion.iniciarColaDeReproduccion(playlistTemporal.getContenido(), 0, usuarioLogueado);
            }
        });

        tarjeta.getChildren().addAll(lblTitulo, lblCantidad, btnPlay);
        return tarjeta;
    }

    /**
     * Crea tarjeta visual para otros productos (Ej: Arte Visual) usando clases de styles.css
     */
    private VBox crearTarjetaProductoComun(Producto prod) {
        VBox tarjeta = new VBox(10);
        tarjeta.setPrefSize(160, 220);
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setPadding(new Insets(15));
        tarjeta.getStyleClass().add("tarjeta-audio");

        Label lblTitulo = new Label(prod.getNombre());
        lblTitulo.getStyleClass().add("label");
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblDetalle = new Label();
        lblDetalle.getStyleClass().add("label-muted");
        lblDetalle.setWrapText(true);

        if (prod instanceof ArteVisualAlbum) {
            ArteVisualAlbum arte = (ArteVisualAlbum) prod;
            lblDetalle.setText("Arte Visual de:\n" + arte.getArtista());
        } else {
            lblDetalle.setText("Producto especial");
        }

        tarjeta.getChildren().addAll(lblTitulo, lblDetalle);
        return tarjeta;
    }

    @FXML
    public void crearNuevaPlaylist(ActionEvent event) {
        if (usuarioLogueado == null || gestorPlaylists == null) return;

        String nombre = txtNombrePlaylist.getText().trim();
        if (nombre.isEmpty()) {
            mostrarAlerta("Campos Vacíos", "Por favor ingresa un nombre válido para la playlist.", Alert.AlertType.WARNING);
            return;
        }

        boolean creada = gestorPlaylists.crearPlaylistParaUsuario(usuarioLogueado, nombre);

        if (creada) {
            if (audioPendiente != null) {
                for (Playlist p : usuarioLogueado.getBiblioteca().getPlaylists()) {
                    if (p.getNombre().equalsIgnoreCase(nombre)) {
                        p.agregarAudio(audioPendiente);
                        break;
                    }
                }
                audioPendiente = null;
                txtNombrePlaylist.setPromptText("Ej. Para el Gym...");
            }
        } else {
            mostrarAlerta("Playlist Duplicada", "Ya posees una lista con ese nombre en tu biblioteca.", Alert.AlertType.ERROR);
        }

        txtNombrePlaylist.clear();
        cargarPlaylistVisuales();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
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
        } catch (Exception e) {
            System.err.println("No se pudo cargar la hoja de estilos en la alerta.");
        }
    }
}