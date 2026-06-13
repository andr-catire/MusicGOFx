package com.proyecto.musicgofx.modelo.entidades;


import java.util.ArrayList;
import java.util.List;

    public class CarritoDeCompras {
        private List<Producto> items;

        public CarritoDeCompras() {
            this.items = new ArrayList<>();
        }

        public void agregarProducto(Producto producto) {

            if (!items.contains(producto)) {
                items.add(producto);
            }
        }

        public void removerProducto(Producto producto) {
            items.remove(producto);
        }

        public double calcularTotal() {
            double total = 0;
            for (Producto p : items) {
                total += p.getPrecio();
            }
            return total;
        }

        public void vaciarCarrito() {
            items.clear();
        }

        public List<Producto> getItems() {
            return items;
        }

        public boolean estaVacio() {
            return items.isEmpty();
        }
}
