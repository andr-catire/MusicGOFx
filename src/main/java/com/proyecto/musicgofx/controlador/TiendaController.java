package com.proyecto.musicgofx.controlador;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import com.proyecto.musicgofx.modelo.entidades.Producto;
import com.proyecto.musicgofx.modelo.entidades.Usuario;
import com.proyecto.musicgofx.modelo.servicios.GestorCatalogo;
import com.proyecto.musicgofx.modelo.servicios.GestorCompras;

public class TiendaController {

    @FXML private FlowPane flowPaneProductos;

    private Usuario usuarioLogueado;
    private GestorCatalogo gestorCatalogo;
    private GestorCompras gestorCompras;
    private MainController mainController;

    public void setDependencias(MainController mainController, GestorCatalogo gc, GestorCompras gcom, Usuario usuario) {
        this.mainController = mainController;
        this.gestorCatalogo = gc;
        this.gestorCompras = gcom;
        this.usuarioLogueado = usuario;

        cargarProductosVisuales();
    }

    public void cargarProductosVisuales() {
        flowPaneProductos.getChildren().clear();

        if (gestorCatalogo == null || gestorCatalogo.getTodosLosProductos() == null || gestorCatalogo.getTodosLosProductos().isEmpty()) {
            Label lblVacio = new Label("No hay productos disponibles en la tienda en este momento.");
            lblVacio.getStyleClass().add("label-muted");
            flowPaneProductos.getChildren().add(lblVacio);
            return;
        }

        for (Producto producto : gestorCatalogo.getTodosLosProductos()) {

            if (mainController != null && mainController.getCarritoActual().contieneProducto(producto)) {
                continue;
            }

            VBox tarjeta = new VBox();
            tarjeta.setPrefSize(180, 200);
            tarjeta.getStyleClass().add("tarjeta-producto");

            Label lblNombre = new Label(producto.getNombre());
            lblNombre.getStyleClass().add("producto-nombre");
            lblNombre.setWrapText(true);
            lblNombre.setMaxHeight(45);

            String tipo = producto.getId().toUpperCase().startsWith("PRD") ? "Paquete Premium" : "Álbum / Contenido";
            Label lblTipo = new Label(tipo);
            lblTipo.getStyleClass().add("producto-tipo");

            Label lblPrecio = new Label(String.format("$%.2f", producto.getPrecio()));
            lblPrecio.getStyleClass().add("producto-precio");

            Button btnAgregar = new Button("🛒 Añadir al carrito");
            btnAgregar.getStyleClass().add("button");
            btnAgregar.setMaxWidth(Double.MAX_VALUE);

            btnAgregar.setOnAction(event -> {
                if (mainController != null) {
                    mainController.agregarAlCarrito(producto);
                    btnAgregar.setText("¡Añadido! ");
                    btnAgregar.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");

                    btnAgregar.setDisable(true);
                }
            });

            VBox.setMargin(btnAgregar, new Insets(10, 0, 0, 0));
            tarjeta.getChildren().addAll(lblNombre, lblTipo, lblPrecio, btnAgregar);
            flowPaneProductos.getChildren().add(tarjeta);
        }
    }
}