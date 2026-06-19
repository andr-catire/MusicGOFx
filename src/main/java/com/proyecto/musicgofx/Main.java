package com.proyecto.musicgofx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * ============================================================
 * MUSICGO - PUNTO DE ENTRADA JAVAFX
 * ============================================================
 */
public class Main extends Application {

    @Override
    public void start(Stage escenarioPrincipal) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/proyecto/musicgofx/Login.fxml"));
        Parent raiz = loader.load();

        Scene escena = new Scene(raiz);

        escenarioPrincipal.setTitle("MusicGO");
        escenarioPrincipal.setScene(escena);

        escenarioPrincipal.setMinWidth(900);
        escenarioPrincipal.setMinHeight(650);

        escenarioPrincipal.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}