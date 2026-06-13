package com.proyecto.musicgofx.modelo.servicios;

import com.proyecto.musicgofx.modelo.entidades.Audio;
import com.proyecto.musicgofx.modelo.entidades.PaqueteTopTen;
import com.proyecto.musicgofx.modelo.entidades.Playlist;
import com.proyecto.musicgofx.modelo.entidades.*;
import com.proyecto.musicgofx.modelo.persistencia.RepositorioDatos;
import com.proyecto.musicgofx.excepciones.SaldoInsuficienteException;
import com.proyecto.musicgofx.excepciones.PagoRechazadoException;

import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Coordina la adquisicion de productos por parte de los usuarios, gestionando
 * la transaccion, el historial del usuario y la persistencia de los datos.
 *
 * @author Equipo MusicGO
 */
public class GestorCompras {

    private final GestorCatalogo gestorCatalogo;
    private final GestorUsuarios gestorUsuarios;
    private final RepositorioDatos repositorio;

    public enum MetodoPago {
        TARJETACRED, TRANSFERENCIA, BILLETERAAPP
    }

    public GestorCompras(GestorCatalogo catalogo, GestorUsuarios usuarios, RepositorioDatos repositorio) {
        this.gestorCatalogo = catalogo;
        this.gestorUsuarios = usuarios;
        this.repositorio = repositorio;
    }

    /**
     * Procesa la compra delegando el pago al metodo correspondiente.
     */
    public void comprarProducto(String idUsuarioOAlias, String idProductoONombre, CarritoDeCompras carrito,MetodoPago metodoPago, String datosPago) throws SaldoInsuficienteException, PagoRechazadoException {
        if (carrito.estaVacio()) {
            System.err.println("Error: El carrito esta vacio. No hay nada que comprar.");
            return;
        }

        Usuario usuario = gestorUsuarios.buscarPorIdOAlias(idUsuarioOAlias);
        if (usuario == null) {
            System.err.println("Error: El usuario '" + idUsuarioOAlias + "' no existe.");
            return;
        }

        double totalAPagar = carrito.calcularTotal();

        switch (metodoPago) {
            case BILLETERAAPP:
                procesarPagoBilletera(usuario, totalAPagar);
                break;
            case TRANSFERENCIA:
                procesarPagoTransferencia(datosPago);
                break;
            case TARJETACRED:
                procesarPagoTarjeta(datosPago);
                break;
        }

        for (Producto producto : carrito.getItems()) {
            Compra nuevaCompra = new Compra(producto.getId(), usuario.getNombre(), producto.getPrecio(), LocalDateTime.now());
            usuario.registrarCompra(nuevaCompra);
            if (producto instanceof PaqueteTopTen) {
                PaqueteTopTen paquete = (PaqueteTopTen) producto;
                Playlist nuevaPlaylist = new Playlist(paquete.getNombre(), usuario.getNombre());

                for (String idCancion : paquete.getIdsCanciones()) {
                    Audio audio = gestorCatalogo.buscarAudioPorId(idCancion);
                    if (audio != null) {
                        nuevaPlaylist.agregarAudio(audio);
                    }
                }
                usuario.getBiblioteca().getPlaylists().add(nuevaPlaylist);
                System.out.println(" > [Sistema] Se ha creado tu nueva playlist: '" + paquete.getNombre() + "' con " + nuevaPlaylist.getContenido().size() + " canciones.");
            }
        }

        System.out.println("\n--- COMPRA DE CARRITO EXITOSA ---");
        System.out.println("Productos comprados: " + carrito.getItems().size());
        System.out.println("Total pagado: $" + totalAPagar);
        System.out.println("Metodo utilizado: " + metodoPago.name());
        System.out.println("---------------------------------\n");

        //
        gestorUsuarios.guardarCambios();
        carrito.vaciarCarrito();
    }

    private void procesarPagoBilletera(Usuario usuario, double total) throws SaldoInsuficienteException {
        if (usuario.getSaldo() < total) {
            throw new SaldoInsuficienteException("Saldo insuficiente en la billetera virtual. Tienes: $" + usuario.getSaldo() + ", Necesitas: $" + total);
        }
        usuario.descontarSaldo(total);
        System.out.println(" > Pago procesado con Billetera App. Nuevo saldo: $" + usuario.getSaldo());
    }

    private void procesarPagoTransferencia(String referencia) throws PagoRechazadoException {
        System.out.println(" > [Aviso] Para transferencias o Pago Movil, utilice los siguientes datos de la empresa:");
        System.out.println("   - Banco: Mercantil (0105)");
        System.out.println("   - RIF: J-41234567-8");
        System.out.println("   - Telefono: 0414-9876543");

        if (referencia == null || referencia.isBlank()) {
            throw new PagoRechazadoException("Error: Debe proporcionar un numero de referencia valido para validar la transferencia.");
        }

        System.out.println(" > Verificando transferencia con referencia N° " + referencia + "... ¡Aprobada!");
    }

    private void procesarPagoTarjeta(String numeroTarjeta) throws PagoRechazadoException {
        if (numeroTarjeta == null || numeroTarjeta.length() < 16) {
            throw new PagoRechazadoException("Error: Numero de tarjeta de credito invalido. Debe contener al menos 16 digitos.");
        }

        String ultimosDigitos = numeroTarjeta.substring(numeroTarjeta.length() - 4);
        System.out.println(" > Conectando con la pasarela de pago para la tarjeta terminada en " + ultimosDigitos + "... ¡Pago Aprobado!");
    }


    public List<Producto> getProductosDisponibles() {
        return gestorCatalogo.getTodosLosProductos();
    }
}