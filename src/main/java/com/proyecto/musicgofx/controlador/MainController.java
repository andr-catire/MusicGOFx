package com.proyecto.musicgofx.controlador;

import com.proyecto.musicgofx.modelo.entidades.Usuario;
import com.proyecto.musicgofx.modelo.entidades.Audio;
import com.proyecto.musicgofx.modelo.entidades.Cancion;
import com.proyecto.musicgofx.modelo.entidades.CarritoDeCompras;
import com.proyecto.musicgofx.modelo.entidades.EpisodioPodcast;
import com.proyecto.musicgofx.modelo.entidades.Playlist;
import com.proyecto.musicgofx.modelo.entidades.Producto; // Añadido para el manejo de productos
import com.proyecto.musicgofx.modelo.servicios.GestorReproduccion;
import com.proyecto.musicgofx.modelo.persistencia.RepositorioDatos;
import com.proyecto.musicgofx.modelo.servicios.GestorCatalogo;
import com.proyecto.musicgofx.modelo.servicios.GestorUsuarios;
import com.proyecto.musicgofx.modelo.servicios.GestorPlaylists;
import com.proyecto.musicgofx.modelo.servicios.GestorExplorador;
import com.proyecto.musicgofx.modelo.servicios.GestorCompras;
import javafx.scene.control.Alert;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.TextField;
import javafx.scene.Node;

public class MainController {

    @FXML private Button btnInicio;
    @FXML private Button btnExplorar;
    @FXML private Button btnBiblioteca;
    @FXML private Button btnTienda;
    @FXML private Button btnAjustes;
    @FXML private TextField txtBuscadorTop;
    @FXML private Button btnPerfilUsuario;
    @FXML private Button btnAgregarAudio;
    @FXML private Button btnAgregarProducto;
    @FXML private Button btnListarUsuario;
    @FXML private Button btnModificarUsuario;
    @FXML private Button btnCambiarRol;
    @FXML private Button btnBilletera;
    @FXML private Button btnCarritoIcono;
    @FXML private AnchorPane contenedorCentral;
    @FXML private Label lblTituloAudio;
    @FXML private Label lblArtistaAudio;
    @FXML private Button btnAnterior;
    @FXML private Button btnPlay;
    @FXML private Button btnSiguiente;

    private Usuario usuarioAutenticado;
    private RepositorioDatos repositorioGlobal;
    private GestorUsuarios gestorUsuarios;
    private GestorCatalogo gestorCatalogo;
    private GestorCompras gestorCompras;
    private GestorReproduccion gestorReproduccion;
    private GestorPlaylists gestorPlaylists;
    private GestorExplorador gestorExplorador;


    private CarritoDeCompras carritoActual = new CarritoDeCompras();

    @FXML
    public void initialize() {
        inicializarGestores();
        configurarNavegacionSidebar();
        configurarNavegacionAdmin();
        configurarBarraSuperior();
        configurarListenersGestor();
        configurarControlesReproductor();
        actualizarBadgeCarrito();

        cargarVista("Inicio.fxml");
    }

    private void inicializarGestores() {
        this.repositorioGlobal = new RepositorioDatos();
        this.gestorUsuarios = new GestorUsuarios(this.repositorioGlobal);
        this.gestorCatalogo = new GestorCatalogo();
        this.gestorCatalogo.cargarDesdeJson();
        this.gestorReproduccion = new GestorReproduccion(this.gestorUsuarios, this.gestorCatalogo);
        this.gestorPlaylists = new GestorPlaylists(this.gestorUsuarios, this.gestorCatalogo);
        this.gestorExplorador = new GestorExplorador();
        this.gestorCompras = new GestorCompras(this.gestorCatalogo, this.gestorUsuarios, this.repositorioGlobal);
    }

    private void configurarNavegacionSidebar() {
        btnInicio.setOnAction(event -> cargarVista("Inicio.fxml"));
        btnExplorar.setOnAction(event -> cargarVista("Explorador.fxml"));
        btnBiblioteca.setOnAction(event -> cargarVista("Biblioteca.fxml"));
        btnTienda.setOnAction(event -> cargarVista("Tienda.fxml"));
        btnAjustes.setOnAction(event -> cargarVista("Ajustes.fxml"));
    }

    private void configurarNavegacionAdmin() {
        if (btnAgregarAudio != null) btnAgregarAudio.setOnAction(event -> cargarVista("AgregarAudio.fxml"));
        if (btnAgregarProducto != null) btnAgregarProducto.setOnAction(event -> cargarVista("AgregarProducto.fxml"));
        if (btnListarUsuario != null) btnListarUsuario.setOnAction(event -> cargarVista("ListarUsuario.fxml"));
        if (btnModificarUsuario != null) btnModificarUsuario.setOnAction(event -> cargarVista("ModificarUsuario.fxml"));
        if (btnCambiarRol != null) btnCambiarRol.setOnAction(event -> cargarVista("CambiarRol.fxml"));
    }

    private void configurarBarraSuperior() {
        if (btnBilletera != null) btnBilletera.setOnAction(event -> cargarVista("Billetera.fxml"));
        if (btnPerfilUsuario != null) btnPerfilUsuario.setOnAction(event -> cargarVista("Ajustes.fxml"));

        if (txtBuscadorTop != null) {
            txtBuscadorTop.setOnMouseClicked(event -> cargarVista("Explorador.fxml"));
            txtBuscadorTop.setOnAction(event -> {
                String textoBusqueda = txtBuscadorTop.getText();
                Object controladorDestino = cargarVista("Explorador.fxml");
                if (controladorDestino instanceof ExploradorController) {
                    ((ExploradorController) controladorDestino).recibirBusqueda(textoBusqueda);
                }
            });
        }
    }

    private void configurarListenersGestor() {
        gestorReproduccion.mensajeAlertaProperty().addListener((observable, mensajeAnterior, mensajeNuevo) -> {
            if (mensajeNuevo != null && !mensajeNuevo.isEmpty()) {
                Alert alertaRestriccion = new Alert(Alert.AlertType.WARNING);
                alertaRestriccion.setTitle("Contenido Restringido");
                alertaRestriccion.setHeaderText("Acceso Denegado por Control Parental");
                alertaRestriccion.setContentText(mensajeNuevo);
                try {
                    String rutaEstiloCss = getClass().getResource("/com/proyecto/musicgofx/styles.css").toExternalForm();
                    alertaRestriccion.getDialogPane().getStylesheets().add(rutaEstiloCss);
                } catch (Exception e) {}
                alertaRestriccion.showAndWait();
                gestorReproduccion.mensajeAlertaProperty().set("");
            }
        });

        gestorReproduccion.audioActualProperty().addListener((observable, cancionAnterior, cancionNueva) -> {
            if (cancionNueva != null) {
                lblTituloAudio.setText(cancionNueva.getTitulo());
                if (cancionNueva instanceof Cancion) {
                    lblArtistaAudio.setText(((Cancion) cancionNueva).getArtista());
                } else if (cancionNueva instanceof EpisodioPodcast) {
                    lblArtistaAudio.setText(((EpisodioPodcast) cancionNueva).getAnfitrion() + " (Podcast)");
                } else {
                    lblArtistaAudio.setText("Autor Desconocido");
                }
            }
        });
    }

    private void configurarControlesReproductor() {
        btnSiguiente.setOnAction(event -> gestorReproduccion.siguiente(usuarioAutenticado));
        btnAnterior.setOnAction(event -> gestorReproduccion.anterior(usuarioAutenticado));
        btnPlay.setOnAction(event -> System.out.println("Botón Play/Pausa presionado"));
        lblTituloAudio.setText("¡Reproduce Ahora!");
        lblArtistaAudio.setText("-");
    }

    public void configurarInterfazSegunRol(Usuario usuario) {
        this.usuarioAutenticado = usuario;
        boolean esAdministrador = (usuario != null && usuario.getRolUsuario() == Usuario.RolUsuario.ADMINISTRADOR);
        Button[] botonesAdministrativos = {btnAgregarAudio, btnAgregarProducto, btnListarUsuario, btnModificarUsuario, btnCambiarRol};
        for (Button boton : botonesAdministrativos) {
            if (boton != null) {
                boton.setVisible(esAdministrador);
                boton.setManaged(esAdministrador);
            }
        }
    }

    public void cambiarVistaAlIniciarSesion(Usuario usuarioRecienLogueado) {
        if (this.gestorUsuarios != null) {
            this.usuarioAutenticado = this.gestorUsuarios.buscarPorIdOAlias(usuarioRecienLogueado.getNombre());
        }
        if (this.usuarioAutenticado == null) {
            this.usuarioAutenticado = usuarioRecienLogueado;
        }
        configurarInterfazSegunRol(this.usuarioAutenticado);
        cargarVista("Inicio.fxml");
    }

    public Object cargarVista(String nombreFxml) {
        try {
            FXMLLoader cargadorVista = new FXMLLoader(getClass().getResource("/com/proyecto/musicgofx/" + nombreFxml));
            Node nodoVistaNueva = cargadorVista.load();
            Object controladorDestino = cargadorVista.getController();

            if (controladorDestino instanceof InicioController) {
                InicioController controladorInicio = (InicioController) controladorDestino;
                controladorInicio.setUsuarioLogueado(this.usuarioAutenticado);
                controladorInicio.setMainController(this);
                controladorInicio.setGestorReproduccion(this.gestorReproduccion);
                controladorInicio.setGestorPlaylists(this.gestorPlaylists);

            } else if (controladorDestino instanceof ExploradorController) {
                ExploradorController controladorExplorador = (ExploradorController) controladorDestino;
                controladorExplorador.setUsuarioActual(this.usuarioAutenticado);
                controladorExplorador.setMainController(this);
                controladorExplorador.setGestorReproduccion(this.gestorReproduccion);
                controladorExplorador.setGestorPlaylists(this.gestorPlaylists);
                controladorExplorador.setGestorExplorador(this.gestorExplorador);

            } else if (controladorDestino instanceof BibliotecaController) {
                BibliotecaController biblioController = (BibliotecaController) controladorDestino;
                biblioController.setMainController(this);
                biblioController.setGestores(this.gestorPlaylists, this.gestorReproduccion);
                biblioController.setUsuarioLogueado(this.usuarioAutenticado);

            } else if (controladorDestino instanceof verContenidoPlaylistController) {

                verContenidoPlaylistController playlistController = (verContenidoPlaylistController) controladorDestino;
                playlistController.setDependencias(this, this.gestorPlaylists, this.gestorReproduccion, this.usuarioAutenticado);
            } else if (controladorDestino instanceof TiendaController) {
                TiendaController tiendaController = (TiendaController) controladorDestino;
                tiendaController.setDependencias(this, this.gestorCatalogo, this.gestorCompras, this.usuarioAutenticado);
            }

            contenedorCentral.getChildren().clear();
            contenedorCentral.getChildren().add(nodoVistaNueva);

            AnchorPane.setTopAnchor(nodoVistaNueva, 0.0);
            AnchorPane.setBottomAnchor(nodoVistaNueva, 0.0);
            AnchorPane.setLeftAnchor(nodoVistaNueva, 0.0);
            AnchorPane.setRightAnchor(nodoVistaNueva, 0.0);

            return controladorDestino;

        } catch (Exception e) {
            System.err.println("Error al cargar la vista: " + nombreFxml);
            e.printStackTrace();
        }
        return null;
    }

    public void abrirVistaPlaylist(Playlist playlistSeleccionada) {
        try {
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("/com/proyecto/musicgofx/verContenidoPlaylist.fxml"));
            Node nodoVista = cargador.load();

            verContenidoPlaylistController playlistController = cargador.getController();
            if (playlistController != null) {
                playlistController.setDependencias(this, this.gestorPlaylists, this.gestorReproduccion, this.usuarioAutenticado);
                playlistController.inicializarConPlaylist(playlistSeleccionada);
            }

            contenedorCentral.getChildren().clear();
            contenedorCentral.getChildren().add(nodoVista);

            AnchorPane.setTopAnchor(nodoVista, 0.0);
            AnchorPane.setBottomAnchor(nodoVista, 0.0);
            AnchorPane.setLeftAnchor(nodoVista, 0.0);
            AnchorPane.setRightAnchor(nodoVista, 0.0);

        } catch (Exception e) {
            System.err.println("Error crítico al abrir la vista detallada 'verContenidoPlaylist.fxml'");
            e.printStackTrace();
        }
    }

    public void irABibliotecaParaNuevaPlaylist(Audio audioPendiente) {
        Object controladorDestino = cargarVista("Biblioteca.fxml");
        if (controladorDestino instanceof BibliotecaController) {
            BibliotecaController controladorBiblioteca = (BibliotecaController) controladorDestino;
            controladorBiblioteca.prepararCreacionConAudio(audioPendiente);
        }
    }


    public CarritoDeCompras getCarritoActual() {
        return this.carritoActual;
    }

    public void agregarAlCarrito(Producto producto) {
        if (producto != null) {
            this.carritoActual.agregarProducto(producto);
            actualizarBadgeCarrito();
        }
    }

    public void actualizarBadgeCarrito() {
        if (btnCarritoIcono != null) {
            if (carritoActual.estaVacio()) {
                btnCarritoIcono.setText("🛒");
            } else {
                btnCarritoIcono.setText("🛒 (" + carritoActual.getItems().size() + ")");
            }
        }
    }

    @FXML
    public void abrirVistaCarrito() {
        System.out.println("Abriendo carrito. Total actual a pagar: $" + carritoActual.calcularTotal());
        // Aquí puedes descomentar la siguiente línea cuando tengas la vista del carrito creada:
        // cargarVista("Carrito.fxml");
    }
}