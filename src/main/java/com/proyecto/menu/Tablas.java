package com.proyecto.menu;

import com.proyecto.modelo.Categoria;
import com.proyecto.modelo.Cliente;
import com.proyecto.modelo.Compra;
import com.proyecto.modelo.DetalleCompra;
import com.proyecto.modelo.DetalleVenta;
import com.proyecto.modelo.Producto;
import com.proyecto.modelo.Proveedor;
import com.proyecto.modelo.Venta;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Formato para imprimir cada tabla en consola.
 * Lo usan los submenús y la opción "Imprimir todas las tablas", así el formato es uno solo.
 */
final class Tablas {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private Tablas() {
        // Clase de utilidad: no se instancia.
    }

    static void titulo(String nombreTabla) {
        System.out.println();
        System.out.println("--- " + nombreTabla + " ---");
    }

    static void imprimirCategorias(List<Categoria> categorias) {
        if (estaVacia(categorias)) {
            return;
        }
        for (Categoria c : categorias) {
            System.out.printf("%d | %s%n", c.idCategoria(), c.nombreCategoria());
        }
        imprimirTotal(categorias.size());
    }

    static void imprimirProductos(List<Producto> productos) {
        if (estaVacia(productos)) {
            return;
        }
        for (Producto p : productos) {
            System.out.printf("%d | %s | %s | $%s | existencias: %d | categoría: %s%n",
                    p.idProducto(), p.sku(), p.nombre(), p.precio(),
                    p.existencias(), p.nombreCategoria());
        }
        imprimirTotal(productos.size());
    }

    static void imprimirClientes(List<Cliente> clientes) {
        if (estaVacia(clientes)) {
            return;
        }
        for (Cliente c : clientes) {
            imprimirCliente(c);
        }
        imprimirTotal(clientes.size());
    }

    static void imprimirCliente(Cliente c) {
        System.out.printf("%d | %s | correo: %s | tel: %s%n",
                c.idCliente(), c.nombreCliente(), texto(c.correoCliente()), texto(c.telefonoCliente()));
    }

    static void imprimirProveedores(List<Proveedor> proveedores) {
        if (estaVacia(proveedores)) {
            return;
        }
        for (Proveedor p : proveedores) {
            imprimirProveedor(p);
        }
        imprimirTotal(proveedores.size());
    }

    static void imprimirProveedor(Proveedor p) {
        System.out.printf("%d | %s | correo: %s | tel: %s%n",
                p.idProveedor(), p.nombreProveedor(), texto(p.correoProveedor()), texto(p.telefonoProveedor()));
    }

    static void imprimirVentas(List<Venta> ventas) {
        if (estaVacia(ventas)) {
            return;
        }
        for (Venta v : ventas) {
            System.out.printf("venta %d | %s | cliente: %d%n", v.idVenta(), fecha(v.fechaVenta()), v.idCliente());
        }
        imprimirTotal(ventas.size());
    }

    static void imprimirCompras(List<Compra> compras) {
        if (estaVacia(compras)) {
            return;
        }
        for (Compra c : compras) {
            System.out.printf("compra %d | %s | proveedor: %d%n", c.idCompra(), fecha(c.fechaCompra()), c.idProveedor());
        }
        imprimirTotal(compras.size());
    }

    /** Imprime los renglones y devuelve la suma de los subtotales. */
    static BigDecimal imprimirDetallesVenta(List<DetalleVenta> detalles) {
        BigDecimal total = BigDecimal.ZERO;
        if (estaVacia(detalles)) {
            return total;
        }
        for (DetalleVenta d : detalles) {
            BigDecimal subtotal = subtotal(d.cantidad(), d.precioUnitario());
            total = total.add(subtotal);
            System.out.printf("venta %d | producto %d | cantidad: %d | $%s c/u | subtotal: $%s%n",
                    d.idVenta(), d.idProducto(), d.cantidad(), d.precioUnitario(), subtotal);
        }
        imprimirTotal(detalles.size());
        return total;
    }

    /** Imprime los renglones y devuelve la suma de los subtotales. */
    static BigDecimal imprimirDetallesCompra(List<DetalleCompra> detalles) {
        BigDecimal total = BigDecimal.ZERO;
        if (estaVacia(detalles)) {
            return total;
        }
        for (DetalleCompra d : detalles) {
            BigDecimal subtotal = subtotal(d.cantidad(), d.costoUnitario());
            total = total.add(subtotal);
            System.out.printf("compra %d | producto %d | cantidad: %d | $%s c/u | subtotal: $%s%n",
                    d.idCompra(), d.idProducto(), d.cantidad(), d.costoUnitario(), subtotal);
        }
        imprimirTotal(detalles.size());
        return total;
    }

    static BigDecimal subtotal(int cantidad, BigDecimal precioUnitario) {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    private static boolean estaVacia(List<?> filas) {
        if (filas.isEmpty()) {
            System.out.println("(sin registros)");
            return true;
        }
        return false;
    }

    private static void imprimirTotal(int filas) {
        System.out.printf("Total: %d registro(s).%n", filas);
    }

    private static String texto(String valor) {
        return valor == null ? "-" : valor;
    }

    /** PostgreSQL guarda la fecha con zona; aquí se muestra en la hora local del equipo. */
    private static String fecha(OffsetDateTime fecha) {
        return fecha == null ? "-" : fecha.atZoneSameInstant(ZoneId.systemDefault()).format(FORMATO_FECHA);
    }
}
