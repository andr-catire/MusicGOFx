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
import  com.proyecto.musicgofx.modelo.servicios.GestorUsuarios;
import  com.proyecto.musicgofx.modelo.entidades.Usuario;
import com.proyecto.musicgofx.excepciones.*;
import com.proyecto.musicgofx.modelo.persistencia.RepositorioDatos;
import javafx.scene.Node;
public class RegistroController {

    @FXML private TextField txtUsuario;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtEdad;
    @FXML private Label lblMensaje;
    private RepositorioDatos repositorio = new RepositorioDatos();
    private GestorUsuarios gestorUsuarios = new GestorUsuarios(repositorio );
    @FXML
    public void procesarRegistro(ActionEvent event) {
        String usuariotxt = txtUsuario.getText().trim();
        String correo = txtCorreo.getText().trim();
        String edadstr = txtEdad.getText().trim();

        if (usuariotxt.isEmpty() || correo.isEmpty() || edadstr.isEmpty()) {
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
            lblMensaje.setText("Error: Todos los campos son obligatorios.");
            return;
        }

        try {
            int edad = Integer.parseInt(edadstr);

            Usuario nuevoUsuario = gestorUsuarios.registrar(usuariotxt , correo, edad);
            lblMensaje.setStyle("-fx-text-fill: #2ecc71;");
            lblMensaje.setText("¡Registro exitoso! Redirigiendo...");
            cargarPantallaPrincipal(nuevoUsuario, event);

        } catch (NumberFormatException e) {
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
            lblMensaje.setText("Error: La edad debe ser un número entero válido.");

        } catch (UsuarioYaExisteException | GmailInvalidoException | EdadValidaException e) {
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
            lblMensaje.setText(e.getMessage());

        } catch (Exception e) {
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
            lblMensaje.setText("Error del sistema al intentar cambiar de pantalla.");
            e.printStackTrace();
        }
    }

    @FXML
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

        @FXML
        public void volverAlLogin(ActionEvent event) {
            try {

                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("/com/proyecto/musicgofx/Login.fxml"));
                stage.setScene(new Scene(root));
                stage.sizeToScene();
                stage.centerOnScreen();

            } catch (Exception e) {
                System.err.println("Error al cargar la pantalla de Login.");
                e.printStackTrace();
            }
        }
}

