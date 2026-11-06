package com.proyecto.menu;

import com.proyecto.Consola;
import com.proyecto.ErroresSql;
import com.proyecto.dao.ClienteDao;
import com.proyecto.dao.ProductoDao;
import com.proyecto.dao.VentaDao;
import com.proyecto.modelo.Cliente;
import com.proyecto.modelo.DetalleVenta;
import com.proyecto.modelo.Producto;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Submenú de clientes y ventas: pide datos, llama a los DAO y muestra resultados.
 */
public class MenuClientesVentas {

    private final Consola consola;
    private final ClienteDao clienteDao;
    private final VentaDao ventaDao;
    private final ProductoDao productoDao;

    public MenuClientesVentas(Consola consola, ClienteDao clienteDao, VentaDao ventaDao, ProductoDao productoDao) {
        this.consola = consola;
        this.clienteDao = clienteDao;
        this.ventaDao = ventaDao;
        this.productoDao = productoDao;
    }

    public void ejecutar() {
        while (true) {
            System.out.println();
            System.out.println("===== GESTIÓN DE CLIENTES Y VENTAS =====");
            System.out.println("1. Listar clientes");
            System.out.println("2. Agregar cliente");
            System.out.println("3. Actualizar cliente");
            System.out.println("4. Eliminar cliente");
            System.out.println("5. Registrar venta");
            System.out.println("6. Listar ventas");
            System.out.println("7. Ver detalle de una venta");
            System.out.println("8. Volver al menú principal");

            switch (consola.leerEntero("Elige una opción: ")) {
                case 1 -> listarClientes();
                case 2 -> agregarCliente();
                case 3 -> actualizarCliente();
                case 4 -> eliminarCliente();
                case 5 -> registrarVenta();
                case 6 -> listarVentas();
                case 7 -> verDetalleVenta();
                case 8 -> {
                    return;
                }
                default -> System.out.println("Opción no válida. Elige un número del 1 al 8.");
            }
        }
    }

    /** Usado por "Imprimir todas las tablas" del menú principal. */
    public void imprimirTablas() {
        Tablas.titulo("clientes");
        listarClientes();
        Tablas.titulo("ventas");
        listarVentas();
        Tablas.titulo("detalle_venta");
        try {
            Tablas.imprimirDetallesVenta(ventaDao.listarDetalles());
        } catch (SQLException e) {
            System.out.println("No se pudo listar detalle_venta: " + e.getMessage());
        }
    }

    // ---------- Clientes ----------

    private void listarClientes() {
        try {
            Tablas.imprimirClientes(clienteDao.listar());
        } catch (SQLException e) {
            System.out.println("No se pudo listar los clientes: " + e.getMessage());
        }
    }

    private void agregarCliente() {
        String nombre = consola.leerTexto("Nombre (máx. 50): ", 50);
        String correo = consola.leerTextoOpcional("Correo (opcional, Enter para omitir): ", 50);
        String telefono = consola.leerTextoOpcional("Teléfono (opcional, Enter para omitir): ", 15);

        try {
            int filas = clienteDao.agregar(nombre, correo, telefono);
            System.out.printf("Cliente agregado. Filas afectadas: %d%n", filas);
        } catch (SQLException e) {
            if (ErroresSql.es(e, ErroresSql.UNICO_VIOLADO)) {
                System.out.println("Ya existe un cliente con ese correo o teléfono.");
            } else {
                System.out.println("No se pudo agregar el cliente: " + e.getMessage());
            }
        }
    }

    private void actualizarCliente() {
        int idCliente = consola.leerEntero("ID del cliente a actualizar: ");

        try {
            Optional<Cliente> actual = clienteDao.buscarPorId(idCliente);
            if (actual.isEmpty()) {
                System.out.println("No existe ningún cliente con ID " + idCliente + ". Filas afectadas: 0");
                return;
            }
            System.out.print("Datos actuales: ");
            Tablas.imprimirCliente(actual.get());

            String nombre = consola.leerTexto("Nuevo nombre (máx. 50): ", 50);
            String correo = consola.leerTextoOpcional("Nuevo correo (Enter = sin correo): ", 50);
            String telefono = consola.leerTextoOpcional("Nuevo teléfono (Enter = sin teléfono): ", 15);

            int filas = clienteDao.actualizar(idCliente, nombre, correo, telefono);
            System.out.printf("Cliente actualizado. Filas afectadas: %d%n", filas);
        } catch (SQLException e) {
            if (ErroresSql.es(e, ErroresSql.UNICO_VIOLADO)) {
                System.out.println("Ese correo o teléfono ya pertenece a otro cliente.");
            } else {
                System.out.println("No se pudo actualizar el cliente: " + e.getMessage());
            }
        }
    }

    private void eliminarCliente() {
        int idCliente = consola.leerEntero("ID del cliente a eliminar: ");

        try {
            int filas = clienteDao.eliminar(idCliente);
            if (filas == 0) {
                System.out.println("No existe ningún cliente con ID " + idCliente + ". Filas afectadas: 0");
            } else {
                System.out.printf("Cliente eliminado. Filas afectadas: %d%n", filas);
            }
        } catch (SQLException e) {
            if (ErroresSql.es(e, ErroresSql.FK_VIOLADA)) {
                System.out.println("No se puede eliminar el cliente " + idCliente + ": tiene ventas registradas.");
                System.out.println("Se conserva el historial. Filas afectadas: 0");
            } else {
                System.out.println("No se pudo eliminar el cliente: " + e.getMessage());
            }
        }
    }

    // ---------- Ventas ----------

    private void registrarVenta() {
        int idCliente = consola.leerEntero("ID del cliente: ");

        try {
            Optional<Cliente> cliente = clienteDao.buscarPorId(idCliente);
            if (cliente.isEmpty()) {
                System.out.println("No existe ningún cliente con ID " + idCliente + ".");
                return;
            }
            System.out.println("Cliente: " + cliente.get().nombreCliente());

            List<DetalleVenta> detalles = new ArrayList<>();
            Set<Integer> productosAgregados = new HashSet<>(); // la PK (id_venta, id_producto) no admite repetidos
            BigDecimal total = BigDecimal.ZERO;

            while (true) {
                String sku = consola.leerTextoOpcional("SKU del producto (Enter para terminar): ", 20);
                if (sku == null) {
                    break;
                }
                Optional<Producto> encontrado = productoDao.buscarPorSku(sku);
                if (encontrado.isEmpty()) {
                    System.out.println("No existe ningún producto con el SKU '" + sku + "'.");
                    continue;
                }
                Producto p = encontrado.get();
                if (productosAgregados.contains(p.idProducto())) {
                    System.out.println("Ese producto ya está en la venta.");
                    continue;
                }
                int cantidad = consola.leerEnteroPositivo(
                        "Cantidad de " + p.nombre() + " (disponibles: " + p.existencias() + "): ");
                if (cantidad > p.existencias()) {
                    System.out.println("No hay existencias suficientes.");
                    continue;
                }

                // El precio unitario se toma del precio actual del producto.
                detalles.add(new DetalleVenta(0, p.idProducto(), cantidad, p.precio()));
                productosAgregados.add(p.idProducto());
                BigDecimal subtotal = Tablas.subtotal(cantidad, p.precio());
                total = total.add(subtotal);
                System.out.printf("Agregado: %d x %s = $%s%n", cantidad, p.nombre(), subtotal);
            }

            if (detalles.isEmpty()) {
                System.out.println("Venta cancelada: no se agregaron productos.");
                return;
            }

            int idVenta = ventaDao.registrarVenta(idCliente, detalles);
            System.out.printf("Venta #%d registrada con %d producto(s). Total: $%s%n",
                    idVenta, detalles.size(), total);
        } catch (SQLException e) {
            if (ErroresSql.es(e, ErroresSql.CHECK_VIOLADO)) {
                System.out.println("No hay existencias suficientes para completar la venta.");
            } else if (ErroresSql.es(e, ErroresSql.FK_VIOLADA)) {
                System.out.println("El cliente o algún producto ya no existe.");
            } else {
                System.out.println("No se pudo registrar la venta: " + e.getMessage());
            }
            System.out.println("La venta no se guardó: se revirtieron todos los cambios.");
        }
    }

    private void listarVentas() {
        try {
            Tablas.imprimirVentas(ventaDao.listar());
        } catch (SQLException e) {
            System.out.println("No se pudo listar las ventas: " + e.getMessage());
        }
    }

    private void verDetalleVenta() {
        int idVenta = consola.leerEntero("ID de la venta: ");

        try {
            List<DetalleVenta> detalles = ventaDao.detallesDe(idVenta);
            if (detalles.isEmpty()) {
                System.out.println("No existe la venta " + idVenta + " o no tiene productos.");
                return;
            }
            BigDecimal total = Tablas.imprimirDetallesVenta(detalles);
            System.out.printf("Total de la venta: $%s%n", total);
        } catch (SQLException e) {
            System.out.println("No se pudo consultar la venta: " + e.getMessage());
        }
    }
}
