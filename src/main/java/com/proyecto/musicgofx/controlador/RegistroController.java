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
import com.proyecto.musicgofx.excepciones.*;

public class RegistroController {

    @FXML private TextField txtUsuario;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtEdad;
    @FXML private Label lblMensaje;
    private GestorUsuarios gestorUsuarios;
    @FXML
    public void procesarRegistro(ActionEvent event) {
        String usuario = txtUsuario.getText().trim();
        String correo = txtCorreo.getText().trim();
        String edadRaw = txtEdad.getText().trim();

        if (usuario.isEmpty() || correo.isEmpty() || edadRaw.isEmpty()) {
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
            lblMensaje.setText("Error: Todos los campos son obligatorios.");
            return;
        }

        try {
            int edad = Integer.parseInt(edadRaw);

            gestorUsuarios.registrar(usuario, correo, edad);
            lblMensaje.setStyle("-fx-text-fill: #2ecc71;");
            lblMensaje.setText("¡Registro exitoso! Redirigiendo...");
            regresarPantallaLogin();

        } catch (NumberFormatException e) {
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
            lblMensaje.setText("Error: La edad debe ser un número entero válido.");

        } catch (UsuarioYaExisteException | GmailInvalidoException | EdadValidaException e) {
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
            lblMensaje.setText(e.getMessage());

        } catch (IOException e) {
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
            lblMensaje.setText("Error del sistema al intentar cambiar de pantalla.");
            e.printStackTrace();
        }
    }

    @FXML
    public void volverAlLogin(ActionEvent event) {
        try {
            regresarPantallaLogin();
        } catch (IOException e) {
            lblMensaje.setText("Error al volver a la pantalla de login.");
        }
    }

    private void regresarPantallaLogin() throws IOException {
        Stage stage = (Stage) txtUsuario.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/vista/Login.fxml"));
        stage.setScene(new Scene(root));
    }
}