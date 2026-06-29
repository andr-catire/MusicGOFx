package com.proyecto.musicgofx.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Pos;
import com.proyecto.musicgofx.modelo.entidades.Producto;
import com.proyecto.musicgofx.modelo.entidades.Usuario;
import com.proyecto.musicgofx.modelo.servicios.GestorCompras;
import com.proyecto.musicgofx.excepciones.PagoRechazadoException;
import com.proyecto.musicgofx.excepciones.SaldoInsuficienteException;
import java.util.List;
import java.util.Optional;

public class CarritodeComprasController {

    private MainController mainController;
    private GestorCompras gestorCompras;
    private Usuario usuarioLogueado;

    @FXML private VBox vboxProductosCarrito;
    @FXML private Label lblTotal;

    @FXML private ToggleGroup grupoMetodosPago;
    @FXML private RadioButton radioTarjeta;
    @FXML private RadioButton radioTransferencia;
    @FXML private RadioButton radioBilletera;

    @FXML private VBox vboxDetallesBilletera;
    @FXML private VBox vboxDetallesTransferencia;
    @FXML private VBox vboxDetallesTarjeta;

    @FXML private Label lblSaldoActual;
    @FXML private TextField txtReferencia;
    @FXML private TextField txtNumeroTarjeta;

    public void setDependencias(MainController main, GestorCompras gestor, Usuario usuario){
        this.mainController = main;
        this.gestorCompras = gestor;
        this.usuarioLogueado = usuario;

        cargarInterfazCarrito();
        configurarAnimacionesDePago();
    }

    /**
     * Este método escucha en tiempo real qué botón de pago se selecciona
     * para ocultar y mostrar los cuadros de texto correspondientes.
     */
    private void configurarAnimacionesDePago() {
        grupoMetodosPago.selectedToggleProperty().addListener((observable, oldVal, newVal) -> {
            // Ocultamos todo por defecto
            vboxDetallesBilletera.setVisible(false); vboxDetallesBilletera.setManaged(false);
            vboxDetallesTransferencia.setVisible(false); vboxDetallesTransferencia.setManaged(false);
            vboxDetallesTarjeta.setVisible(false); vboxDetallesTarjeta.setManaged(false);

            if (newVal == radioBilletera) {
                lblSaldoActual.setText("Saldo disponible en Billetera: $" + String.format("%.2f", usuarioLogueado.getSaldo()));
                vboxDetallesBilletera.setVisible(true); vboxDetallesBilletera.setManaged(true);

            } else if (newVal == radioTransferencia) {
                vboxDetallesTransferencia.setVisible(true); vboxDetallesTransferencia.setManaged(true);

            } else if (newVal == radioTarjeta) {
                vboxDetallesTarjeta.setVisible(true); vboxDetallesTarjeta.setManaged(true);
            }
        });
    }

    public void cargarInterfazCarrito(){
        vboxProductosCarrito.getChildren().clear();

        if(mainController.getCarritoActual().estaVacio()){
            lblTotal.setText("Total: $0.00");
            Label carritovacio = new Label("Tu Carrito está vacío. ¡Ve a la tienda!");
            carritovacio.getStyleClass().add("label-muted");
            vboxProductosCarrito.getChildren().add(carritovacio);
            return;
        }

        List<Producto> Productos = mainController.getCarritoActual().getItems();

        for(Producto p : Productos){
            HBox fila = new HBox();
            fila.setAlignment(Pos.CENTER_LEFT);
            fila.getStyleClass().add("cart-item-row");

            Label lblNombre = new Label(p.getNombre());
            lblNombre.getStyleClass().add("player-title");

            Label lblPrecio = new Label("$" + p.getPrecio());
            lblPrecio.getStyleClass().add("cart-item-price");

            Region espaciador = new Region();
            HBox.setHgrow(espaciador, Priority.ALWAYS);

            Button btnRemover = new Button("✕");
            btnRemover.getStyleClass().add("btn-eliminar-fila");
            btnRemover.setOnAction(e -> {
                mainController.getCarritoActual().removerProducto(p);
                mainController.actualizarBadgeCarrito();
                cargarInterfazCarrito();
            });

            fila.getChildren().addAll(lblNombre, lblPrecio, espaciador, btnRemover);
            vboxProductosCarrito.getChildren().add(fila);
        }

        lblTotal.setText("Total: $" + String.format("%.2f", mainController.getCarritoActual().calcularTotal()));
    }

    @FXML
    public void procesarFinalizarCompra(){
        if (mainController.getCarritoActual().estaVacio()){
            mostrarAlerta("Atención", "El Carrito de Compras está vacío.", Alert.AlertType.WARNING);
            return;
        }

        RadioButton seleccionado = (RadioButton) grupoMetodosPago.getSelectedToggle();
        if (seleccionado == null) {
            mostrarAlerta("Método de Pago", "Por favor, selecciona un método de pago.", Alert.AlertType.WARNING);
            return;
        }

        GestorCompras.MetodoPago metodoSeleccionado = null;
        String datoExtra = "";

        if (seleccionado == radioBilletera) {
            metodoSeleccionado = GestorCompras.MetodoPago.BILLETERAAPP;

        } else if (seleccionado == radioTransferencia) {
            metodoSeleccionado = GestorCompras.MetodoPago.TRANSFERENCIA;
            datoExtra = txtReferencia.getText().trim();
            if (datoExtra.isEmpty()) {
                mostrarAlerta("Falta Información", "Debe ingresar el número de referencia del Pago Móvil.", Alert.AlertType.WARNING);
                return;
            }

        } else if (seleccionado == radioTarjeta) {
            metodoSeleccionado = GestorCompras.MetodoPago.TARJETACRED;
            datoExtra = txtNumeroTarjeta.getText().trim();
            if (datoExtra.length() < 16) {
                mostrarAlerta("Tarjeta Inválida", "El número de tarjeta debe contener al menos 16 dígitos.", Alert.AlertType.WARNING);
                return;
            }
        }

        try {
            // Se comunica perfectamente con la firma refactorizada de tu GestorCompras
            gestorCompras.procesarCompra(usuarioLogueado, mainController.getCarritoActual().getItems(), metodoSeleccionado, datoExtra);

            mainController.getCarritoActual().vaciarCarrito();
            mainController.actualizarBadgeCarrito();

            mostrarAlerta("¡Compra Realizada con Éxito!", "Gracias por tu compra. Tus productos ya están en tu cuenta.", Alert.AlertType.INFORMATION);
            mainController.cargarVista("Biblioteca.fxml");

        } catch (SaldoInsuficienteException e) {
            Alert alertaRecarga = new Alert(Alert.AlertType.CONFIRMATION);
            alertaRecarga.setTitle("Saldo Insuficiente");
            alertaRecarga.setHeaderText(null);
            alertaRecarga.setContentText(e.getMessage() + "\n¿Deseas ir a tu billetera para recargar saldo?");
            aplicarEstiloOscuro(alertaRecarga.getDialogPane());
            ButtonType btnRecargar = new ButtonType("Ir a Recargar");
            ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
            alertaRecarga.getButtonTypes().setAll(btnRecargar, btnCancelar);

            Optional<ButtonType> resultado = alertaRecarga.showAndWait();
            if (resultado.isPresent() && resultado.get() == btnRecargar) {
                mainController.cargarVista("Billetera.fxml");
            }

        } catch (PagoRechazadoException e) {
            mostrarAlerta("Pago", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Error", "Ocurrió un problema inesperado: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        aplicarEstiloOscuro(alerta.getDialogPane());
        alerta.showAndWait();
    }

    private void aplicarEstiloOscuro(javafx.scene.control.DialogPane panel) {
        try {
            String rutaCss = getClass().getResource("/com/proyecto/musicgofx/styles.css").toExternalForm();
            panel.getStylesheets().add(rutaCss);
            panel.getStyleClass().add("ventana-emergente-oscura");
        } catch (Exception e) {
            System.err.println("No se pudo aplicar el estilo a la alerta.");
        }
    }
}