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
import com.proyecto.musicgofx.modelo.servicios.GestorUsuarios;
import com.proyecto.musicgofx.excepciones.*;
import com.proyecto.musicgofx.modelo.entidades.Usuario;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private TextField txtCorreo;
    @FXML private Label lblMensaje;
    private GestorUsuarios gestorUsuarios;
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

            // Aquí irá tu código para abrir el MainLayout.fxml (la app con la Sidebar)
            // cargarPantallaPrincipal(usuarioLogueado);

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
            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/vista/Registro.fxml"));
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            lblMensaje.setText("Error al cargar la pantalla de registro.");
            e.printStackTrace();
        }
    }
}