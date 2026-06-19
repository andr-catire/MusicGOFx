package com.proyecto.musicgofx.controlador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;

import com.proyecto.musicgofx.modelo.servicios.GestorUsuarios;
import com.proyecto.musicgofx.modelo.persistencia.RepositorioDatos;
import com.proyecto.musicgofx.excepciones.*;
import com.proyecto.musicgofx.modelo.entidades.Usuario;
import javafx.scene.Node;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private TextField txtCorreo;
    @FXML private Label lblMensaje;
    @FXML private  Button btnAccion;
    @FXML  private Hyperlink linkRegistro;
    private RepositorioDatos repositorio = new RepositorioDatos();
    private GestorUsuarios gestorUsuarios = new GestorUsuarios(repositorio);
    @FXML
    public void manejarBotonPrincipal(ActionEvent event) {
        String usuarioInput = txtUsuario.getText().trim();
        String correoInput = txtCorreo.getText().trim();

        if (usuarioInput.isEmpty() || correoInput.isEmpty()) {
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText("Por favor, llena todos los campos.");
            return;
        }

        try {
            Usuario usuarioLogueado = gestorUsuarios.iniciarSesion(usuarioInput, correoInput);
            lblMensaje.setStyle("-fx-text-fill: #2ecc71;");
            lblMensaje.setText("¡Bienvenido " + usuarioLogueado.getNombre() + "!");

            cargarPantallaPrincipal(usuarioLogueado, event);

        } catch (UsuarioNoEncontradoException | GmailInvalidoException e) {
            lblMensaje.setText(e.getMessage() + " ¿Deseas registrarte?");
            lblMensaje.setStyle("-fx-text-fill: red;");
            txtUsuario.setStyle(txtUsuario.getStyle() + "-fx-border-color: #ff4d4d;");

        } catch (Exception e) {
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText("Error técnico al conectar con la base de datos.");
            e.printStackTrace();
        }
    }

    @FXML
    public void irAPantallaRegistro(ActionEvent event) {
        try {
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            Parent root = FXMLLoader.load(getClass().getResource("/com/proyecto/musicgofx/Registro.fxml"));
            stage.setScene(new Scene(root));
            stage.centerOnScreen();

        } catch (IOException e) {
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText("Error al cargar el archivo FXML de registro.");
            e.printStackTrace();
        } catch (Exception e) {
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText("Error inesperado en el controlador.");
            e.printStackTrace();
        }
    }

    private void cargarPantallaPrincipal(Usuario usuario, ActionEvent event) {
        try {
            Scene escenaActual = ((Node) event.getSource()).getScene();

            MainController mainController = (MainController) escenaActual.getUserData();

            if (mainController != null) {
                mainController.cambiarVistaAlIniciarSesion(usuario);
            } else {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/proyecto/musicgofx/MainLayout.fxml"));
                Stage stage = (Stage) escenaActual.getWindow();
                Parent root = loader.load();
                MainController mc = loader.getController();
                mc.configurarInterfazSegunRol(usuario);

                stage.setScene(new Scene(root));
            }
        } catch (Exception e) {
            lblMensaje.setText("Error al redirigir a la pantalla principal.");
            e.printStackTrace();
        }
    }
}