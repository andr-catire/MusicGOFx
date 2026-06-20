package com.proyecto.musicgofx.controlador;

import com.proyecto.musicgofx.modelo.entidades.Usuario;
import com.proyecto.musicgofx.modelo.entidades.Audio;
import com.proyecto.musicgofx.modelo.entidades.Cancion;
import com.proyecto.musicgofx.modelo.entidades.EpisodioPodcast;
import com.proyecto.musicgofx.modelo.servicios.GestorReproduccion;
import com.proyecto.musicgofx.modelo.persistencia.RepositorioDatos;
import com.proyecto.musicgofx.modelo.servicios.GestorCatalogo;
import com.proyecto.musicgofx.modelo.servicios.GestorUsuarios;
import javafx.scene.control.Alert;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.TextField;
import javafx.scene.Node;

import java.io.IOException;

/**
 * Controlador principal de la aplicación (MainLayout).
 * Gestiona la navegación del menú lateral, la barra superior con la billetera,
 * el reproductor de audio, la inyección de vistas en el contenedor central
 * y el control de acceso basado en los roles de usuario.
 */
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

    @FXML private AnchorPane contenedorCentral;
    @FXML private Label lblTituloAudio;
    @FXML private Label lblArtistaAudio;
    @FXML private Button btnAnterior;
    @FXML private Button btnPlay;
    @FXML private Button btnSiguiente;

    private Usuario usuarioLogueado;
    private GestorReproduccion gestorReproduccion;
    private RepositorioDatos repositorio = new RepositorioDatos();

    /**
     * Carga un archivo FXML y lo incrusta dinámicamente en el contenedor central.
     * @param nombreFxml El nombre del archivo FXML a cargar.
     * @return El controlador asociado a la vista cargada, o null si ocurre un error.
     */
    private Object cargarVista(String nombreFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/proyecto/musicgofx/" + nombreFxml));

            javafx.scene.Node nuevaVista = loader.load();
            Object controlador = loader.getController();

            if (controlador instanceof InicioController) {
                InicioController inicioCtrl = (InicioController) controlador;
                inicioCtrl.setUsuarioLogueado(this.usuarioLogueado);
                inicioCtrl.setGestorReproduccion(this.gestorReproduccion);
            } else if (controlador instanceof ExploradorController) {
                ExploradorController exploradorCtrl = (ExploradorController) controlador;
                exploradorCtrl.setGestorReproduccion(this.gestorReproduccion);
                exploradorCtrl.setUsuarioActual(this.usuarioLogueado);
            }

            contenedorCentral.getChildren().clear();
            contenedorCentral.getChildren().add(nuevaVista);

            AnchorPane.setTopAnchor(nuevaVista, 0.0);
            AnchorPane.setBottomAnchor(nuevaVista, 0.0);
            AnchorPane.setLeftAnchor(nuevaVista, 0.0);
            AnchorPane.setRightAnchor(nuevaVista, 0.0);

            return controlador;

        } catch (IOException e) {
            System.err.println("No se pudo cargar la vista: " + nombreFxml);
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("No se encontró el archivo FXML. Revisa la ruta: /com/proyecto/musicgofx/" + nombreFxml);
        }
        return null;
    }

    /**
     * Inicializa los eventos de navegación de todos los botones de la interfaz
     * al cargar la vista principal.
     */
    @FXML
    public void initialize() {

        inicializarGestores();
        configurarNavegacionSidebar();
        configurarNavegacionAdmin();
        configurarBarraSuperior();
        configurarListenersGestor();
        configurarControlesReproductor();
        cargarVista("Inicio.fxml");
    }
    // ════════════════════════════════════════════════════
// SUBMÉTODOS DE INICIALIZACIÓN
// ════════════════════════════════════════════════════

    private void inicializarGestores() {
        GestorCatalogo catalogoGlobal = new GestorCatalogo();
        catalogoGlobal.cargarDesdeJson();
        this.gestorReproduccion = new GestorReproduccion(new GestorUsuarios(repositorio), catalogoGlobal);
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
                String busqueda = txtBuscadorTop.getText();
                System.out.println("Buscando en catálogo: " + busqueda);

                Object controlador = cargarVista("Explorador.fxml");
                if (controlador instanceof ExploradorController) {
                    ExploradorController exploradorCtrl = (ExploradorController) controlador;
                    exploradorCtrl.recibirBusqueda(busqueda);
                }
            });
        }
    }

    private void configurarListenersGestor() {
        gestorReproduccion.mensajeAlertaProperty().addListener((observable, viejoMensaje, nuevoMensaje) -> {
            if (nuevoMensaje != null && !nuevoMensaje.isEmpty()) {
                Alert alerta = new Alert(javafx.scene.control.Alert.AlertType.WARNING);
                alerta.setTitle("Contenido Restringido");
                alerta.setHeaderText("Acceso Denegado por Control Parental");
                alerta.setContentText(nuevoMensaje);
                try {
                    String rutaCss = getClass().getResource("/com/proyecto/musicgofx/styles.css").toExternalForm();
                    alerta.getDialogPane().getStylesheets().add(rutaCss);
                } catch (Exception e) {
                    System.out.println("Aviso: No se encontró el CSS, pero la alerta se mostrará igual.");
                }
                alerta.showAndWait();
                gestorReproduccion.mensajeAlertaProperty().set("");
            }
        });
        gestorReproduccion.audioActualProperty().addListener((observable, cancionVieja, cancionNueva) -> {
            if (cancionNueva != null) {
                lblTituloAudio.setText(cancionNueva.getTitulo());
                if (cancionNueva instanceof Cancion) {
                    lblArtistaAudio.setText(((Cancion) cancionNueva).getArtista());
                } else if (cancionNueva instanceof EpisodioPodcast) {
                    lblArtistaAudio.setText(((EpisodioPodcast) cancionNueva).getAnfitrion() + " (Podcast)");
                } else {
                    lblArtistaAudio.setText("Desconocido");
                }
            }
        });
    }

    private void configurarControlesReproductor() {
        btnSiguiente.setOnAction(event -> {
            gestorReproduccion.siguiente(usuarioLogueado);
        });

        btnAnterior.setOnAction(event -> {
            gestorReproduccion.anterior(usuarioLogueado);
        });

        btnPlay.setOnAction(event -> {
            System.out.println("Botón Play/Pausa presionado");
        });
        lblTituloAudio.setText("Reproduce Ahora!!");
        lblArtistaAudio.setText("-");
    }

    /**
     * Configura la visibilidad de los botones de administración en la barra lateral
     * basándose en el rol del usuario autenticado.
     * @param usuario El objeto Usuario que acaba de iniciar sesión.
     */
    public void configurarInterfazSegunRol(Usuario usuario) {
        this.usuarioLogueado = usuario;

        boolean esAdministrador = false;
        if (usuario != null && usuario.getRolUsuario() != null) {
            esAdministrador = usuario.getRolUsuario() == Usuario.RolUsuario.ADMINISTRADOR;
        }

        Button[] botonesAdmin = {
                btnAgregarAudio,
                btnAgregarProducto,
                btnListarUsuario,
                btnModificarUsuario,
                btnCambiarRol
        };

        for (Button btn : botonesAdmin) {
            if (btn != null) {
                btn.setVisible(esAdministrador);
                btn.setManaged(esAdministrador);
            }
        }
    }

    public void cambiarVistaAlIniciarSesion(Usuario usuario) {
        this.usuarioLogueado = usuario;
        configurarInterfazSegunRol(usuario);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/proyecto/musicgofx/Inicio.fxml"));
            Node nodoInicio = loader.load();

            InicioController inicioCtrl = loader.getController();
            inicioCtrl.setUsuarioLogueado(usuario);
            inicioCtrl.setGestorReproduccion(this.gestorReproduccion);
            contenedorCentral.getChildren().setAll(nodoInicio);

        } catch (IOException e) {
            System.err.println("Error al cargar Inicio.fxml despues del login");
            e.printStackTrace();
        }
    }
}