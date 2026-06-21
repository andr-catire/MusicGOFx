package com.proyecto.musicgofx.controlador;

import com.proyecto.musicgofx.modelo.entidades.Usuario;
import com.proyecto.musicgofx.modelo.entidades.Audio;
import com.proyecto.musicgofx.modelo.entidades.Cancion;
import com.proyecto.musicgofx.modelo.entidades.EpisodioPodcast;
import com.proyecto.musicgofx.modelo.servicios.GestorReproduccion;
import com.proyecto.musicgofx.modelo.persistencia.RepositorioDatos;
import com.proyecto.musicgofx.modelo.servicios.GestorCatalogo;
import com.proyecto.musicgofx.modelo.servicios.GestorUsuarios;
import com.proyecto.musicgofx.modelo.servicios.GestorPlaylists;
import com.proyecto.musicgofx.modelo.servicios.GestorExplorador;
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
 * Actúa como el Gran Coordinador: Gestiona la navegación, instancía los gestores
 * principales y los inyecta en las vistas secundarias respetando el SRP.
 */
public class MainController {

    // ════════════════════════════════════════════════════
    // COMPONENTES DE INTERFAZ (VISTA)
    // ════════════════════════════════════════════════════
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

    // ════════════════════════════════════════════════════
    // MODELOS Y GESTORES (LÓGICA DE NEGOCIO)
    // ════════════════════════════════════════════════════
    private Usuario usuarioAutenticado;

    private RepositorioDatos repositorioGlobal;
    private GestorUsuarios gestorUsuarios;
    private GestorCatalogo gestorCatalogo;
    private GestorReproduccion gestorReproduccion;
    private GestorPlaylists gestorPlaylists;
    private GestorExplorador gestorExplorador;

    /**
     * Inicializa los eventos y gestores al arrancar la aplicación.
     */
    @FXML
    public void initialize() {
        inicializarGestores();
        configurarNavegacionSidebar();
        configurarNavegacionAdmin();
        configurarBarraSuperior();
        configurarListenersGestor();
        configurarControlesReproductor();

        // Vista por defecto al abrir (aunque luego el login cambie el estado)
        cargarVista("Inicio.fxml");
    }

    // ════════════════════════════════════════════════════
    // MÉTODOS DE INICIALIZACIÓN Y CONFIGURACIÓN
    // ════════════════════════════════════════════════════

    /**
     * Instancia todos los gestores centrales una sola vez y maneja sus dependencias.
     */
    private void inicializarGestores() {
        this.repositorioGlobal = new RepositorioDatos();
        this.gestorUsuarios = new GestorUsuarios(this.repositorioGlobal);

        this.gestorCatalogo = new GestorCatalogo();
        this.gestorCatalogo.cargarDesdeJson();

        this.gestorReproduccion = new GestorReproduccion(this.gestorUsuarios, this.gestorCatalogo);
        this.gestorPlaylists = new GestorPlaylists(this.gestorUsuarios, this.gestorCatalogo);
        this.gestorExplorador = new GestorExplorador();
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
                System.out.println("Buscando en catálogo: " + textoBusqueda);

                Object controladorDestino = cargarVista("Explorador.fxml");
                if (controladorDestino instanceof ExploradorController) {
                    ExploradorController controladorExplorador = (ExploradorController) controladorDestino;
                    controladorExplorador.recibirBusqueda(textoBusqueda);
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
                } catch (Exception e) {
                    System.out.println("Aviso: No se encontró el CSS, pero la alerta se mostrará igual.");
                }
                alertaRestriccion.showAndWait();
                gestorReproduccion.mensajeAlertaProperty().set(""); // Limpiar mensaje
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

        Button[] botonesAdministrativos = {
                btnAgregarAudio, btnAgregarProducto, btnListarUsuario,
                btnModificarUsuario, btnCambiarRol
        };

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


    /**
     * Carga un FXML, lo incrusta en el contenedor central y le inyecta las
     * dependencias (Gestores) necesarias a su respectivo controlador.
     */
    private Object cargarVista(String nombreFxml) {
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
            }
            // NOTA: Aquí inyectarás a BibliotecaController en el futuro.

            contenedorCentral.getChildren().clear();
            contenedorCentral.getChildren().add(nodoVistaNueva);

            AnchorPane.setTopAnchor(nodoVistaNueva, 0.0);
            AnchorPane.setBottomAnchor(nodoVistaNueva, 0.0);
            AnchorPane.setLeftAnchor(nodoVistaNueva, 0.0);
            AnchorPane.setRightAnchor(nodoVistaNueva, 0.0);

            return controladorDestino;

        } catch (IOException e) {
            System.err.println("Error de I/O al cargar la vista: " + nombreFxml);
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("No se encontró el archivo FXML. Revisa la ruta: /com/proyecto/musicgofx/" + nombreFxml);
        }
        return null;
    }

    /**
     * Permite a las sub-vistas solicitar un cambio hacia la Biblioteca,
     * llevando consigo un audio pendiente por guardar.
     */
    public void irABibliotecaParaNuevaPlaylist(Audio audioPendiente) {
        Object controladorDestino = cargarVista("Biblioteca.fxml");

        // Próximamente lo descomentarás cuando programes el BibliotecaController
        /*
        if (controladorDestino instanceof BibliotecaController) {
            BibliotecaController controladorBiblioteca = (BibliotecaController) controladorDestino;
            controladorBiblioteca.prepararCreacionConAudio(audioPendiente);
        }
        */
    }
}