package com.proyecto.musicgofx.controlador;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;


import java.io.IOException;
public class MainController {
    @FXML private Button btnInicio;
    @FXML private Button btnExplorar;
    @FXML private Button btnBiblioteca;
    @FXML private Button btnTienda;
    @FXML private Button btnAjustes;
    @FXML private AnchorPane contenedorCentral;
    @FXML private Label lblTituloAudio;
    @FXML private Label lblArtistaAudio;
    @FXML private Button btnAnterior;
    @FXML private Button btnPlay;
    @FXML private Button btnSiguiente;

    private void cargarVista( String nombreFxml){
        try{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + nombreFxml));
        AnchorPane nuevaVista = loader.load();
        contenedorCentral.getChildren().clear();
        contenedorCentral.getChildren().add(nuevaVista);
        AnchorPane.setTopAnchor(nuevaVista, 0.0);
        AnchorPane.setBottomAnchor(nuevaVista, 0.0);
        AnchorPane.setLeftAnchor(nuevaVista, 0.0);
        AnchorPane.setRightAnchor(nuevaVista, 0.0);
        } catch (IOException e) {
        System.err.println("No se pudo cargar la vista: " + nombreFxml);
            e.printStackTrace();
        } catch (NullPointerException e) {
        System.err.println("No se encontró el archivo FXML. Revisa la ruta: " + nombreFxml);
        }
    }
    @FXML
    public void initialize(){
        btnInicio.setOnAction(event -> cargarVista("Inicio.fxml"));
        btnExplorar.setOnAction(event -> cargarVista("Explorar.fxml"));
        btnBiblioteca.setOnAction(event -> cargarVista("Biblioteca.fxml"));
        btnTienda.setOnAction(event -> cargarVista("Tienda.fxml"));
        btnAjustes.setOnAction(event -> cargarVista("Ajustes.fxml"));
        btnPlay.setOnAction(event -> System.out.println("Botón Play presionado"));
        btnSiguiente.setOnAction(event -> System.out.println("Siguiente canción..."));
        btnAnterior.setOnAction(event -> System.out.println("Canción anterior..."));
        lblTituloAudio.setText("Ninguna canción reproduciéndose");
        lblArtistaAudio.setText("-");
    }

}
