package com.proyecto.menu;

import com.proyecto.Consola;
import com.proyecto.ErroresSql;
import com.proyecto.dao.CompraDao;
import com.proyecto.dao.ProductoDao;
import com.proyecto.dao.ProveedorDao;
import com.proyecto.modelo.DetalleCompra;
import com.proyecto.modelo.Producto;
import com.proyecto.modelo.Proveedor;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Submenú de proveedores y compras: pide datos, llama a los DAO y muestra resultados.
 */
public class MenuProveedoresCompras {

    private final Consola consola;
    private final ProveedorDao proveedorDao;
    private final CompraDao compraDao;
    private final ProductoDao productoDao;

    public MenuProveedoresCompras(Consola consola, ProveedorDao proveedorDao, CompraDao compraDao,
                                  ProductoDao productoDao) {
        this.consola = consola;
        this.proveedorDao = proveedorDao;
        this.compraDao = compraDao;
        this.productoDao = productoDao;
    }

    public void ejecutar() {
        while (true) {
            System.out.println();
            System.out.println("===== GESTIÓN DE PROVEEDORES Y COMPRAS =====");
            System.out.println("1. Listar proveedores");
            System.out.println("2. Agregar proveedor");
            System.out.println("3. Actualizar proveedor");
            System.out.println("4. Eliminar proveedor");
            System.out.println("5. Registrar compra");
            System.out.println("6. Listar compras");
            System.out.println("7. Ver detalle de una compra");
            System.out.println("8. Volver al menú principal");

            switch (consola.leerEntero("Elige una opción: ")) {
                case 1 -> listarProveedores();
                case 2 -> agregarProveedor();
                case 3 -> actualizarProveedor();
                case 4 -> eliminarProveedor();
                case 5 -> registrarCompra();
                case 6 -> listarCompras();
                case 7 -> verDetalleCompra();
                case 8 -> {
                    return;
                }
                default -> System.out.println("Opción no válida. Elige un número del 1 al 8.");
            }
        }
    }

    /** Usado por "Imprimir todas las tablas" del menú principal. */
    public void imprimirTablas() {
        Tablas.titulo("proveedores");
        listarProveedores();
        Tablas.titulo("compras");
        listarCompras();
        Tablas.titulo("detalle_compra");
        try {
            Tablas.imprimirDetallesCompra(compraDao.listarDetalles());
        } catch (SQLException e) {
            System.out.println("No se pudo listar detalle_compra: " + e.getMessage());
        }
    }

    // ---------- Proveedores ----------

    private void listarProveedores() {
        try {
            Tablas.imprimirProveedores(proveedorDao.listar());
        } catch (SQLException e) {
            System.out.println("No se pudo listar los proveedores: " + e.getMessage());
        }
    }

    /**
     * Pide correo y teléfono hasta que haya al menos uno, porque el esquema lo exige
     * (CHECK correo_proveedor IS NOT NULL OR telefono_proveedor IS NOT NULL).
     *
     * @return arreglo {correo, telefono}; uno de los dos puede ser null.
     */
    private String[] leerContactoProveedor() {
        while (true) {
            String correo = consola.leerTextoOpcional("Correo (Enter para omitir): ", 50);
            String telefono = consola.leerTextoOpcional("Teléfono (Enter para omitir): ", 15);
            if (correo != null || telefono != null) {
                return new String[] {correo, telefono};
            }
            System.out.println("Debes escribir al menos el correo o el teléfono.");
        }
    }

    private void agregarProveedor() {
        String nombre = consola.leerTexto("Nombre (máx. 50): ", 50);
        String[] contacto = leerContactoProveedor();

        try {
            int filas = proveedorDao.agregar(nombre, contacto[0], contacto[1]);
            System.out.printf("Proveedor agregado. Filas afectadas: %d%n", filas);
        } catch (SQLException e) {
            if (ErroresSql.es(e, ErroresSql.UNICO_VIOLADO)) {
                System.out.println("Ya existe un proveedor con ese correo o teléfono.");
            } else if (ErroresSql.es(e, ErroresSql.CHECK_VIOLADO)) {
                System.out.println("El proveedor necesita al menos correo o teléfono.");
            } else {
                System.out.println("No se pudo agregar el proveedor: " + e.getMessage());
            }
        }
    }

    private void actualizarProveedor() {
        int idProveedor = consola.leerEntero("ID del proveedor a actualizar: ");

        try {
            Optional<Proveedor> actual = proveedorDao.buscarPorId(idProveedor);
            if (actual.isEmpty()) {
                System.out.println("No existe ningún proveedor con ID " + idProveedor + ". Filas afectadas: 0");
                return;
            }
            System.out.print("Datos actuales: ");
            Tablas.imprimirProveedor(actual.get());

            String nombre = consola.leerTexto("Nuevo nombre (máx. 50): ", 50);
            String[] contacto = leerContactoProveedor();

            int filas = proveedorDao.actualizar(idProveedor, nombre, contacto[0], contacto[1]);
            System.out.printf("Proveedor actualizado. Filas afectadas: %d%n", filas);
        } catch (SQLException e) {
            if (ErroresSql.es(e, ErroresSql.UNICO_VIOLADO)) {
                System.out.println("Ese correo o teléfono ya pertenece a otro proveedor.");
            } else if (ErroresSql.es(e, ErroresSql.CHECK_VIOLADO)) {
                System.out.println("El proveedor necesita al menos correo o teléfono.");
            } else {
                System.out.println("No se pudo actualizar el proveedor: " + e.getMessage());
            }
        }
    }

    private void eliminarProveedor() {
        int idProveedor = consola.leerEntero("ID del proveedor a eliminar: ");

        try {
            int filas = proveedorDao.eliminar(idProveedor);
            if (filas == 0) {
                System.out.println("No existe ningún proveedor con ID " + idProveedor + ". Filas afectadas: 0");
            } else {
                System.out.printf("Proveedor eliminado. Filas afectadas: %d%n", filas);
            }
        } catch (SQLException e) {
            if (ErroresSql.es(e, ErroresSql.FK_VIOLADA)) {
                System.out.println("No se puede eliminar el proveedor " + idProveedor + ": tiene compras registradas.");
                System.out.println("Se conserva el historial. Filas afectadas: 0");
            } else {
                System.out.println("No se pudo eliminar el proveedor: " + e.getMessage());
            }
        }
    }

    // ---------- Compras ----------

    private void registrarCompra() {
        int idProveedor = consola.leerEntero("ID del proveedor: ");

        try {
            Optional<Proveedor> proveedor = proveedorDao.buscarPorId(idProveedor);
            if (proveedor.isEmpty()) {
                System.out.println("No existe ningún proveedor con ID " + idProveedor + ".");
                return;
            }
            System.out.println("Proveedor: " + proveedor.get().nombreProveedor());

            List<DetalleCompra> detalles = new ArrayList<>();
            Set<Integer> productosAgregados = new HashSet<>(); // la PK (id_compra, id_producto) no admite repetidos
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
                    System.out.println("Ese producto ya está en la compra.");
                    continue;
                }
                int cantidad = consola.leerEnteroPositivo("Cantidad de " + p.nombre() + ": ");
                BigDecimal costo = consola.leerDecimalNoNegativo("Costo unitario: ");

                detalles.add(new DetalleCompra(0, p.idProducto(), cantidad, costo));
                productosAgregados.add(p.idProducto());
                BigDecimal subtotal = Tablas.subtotal(cantidad, costo);
                total = total.add(subtotal);
                System.out.printf("Agregado: %d x %s = $%s%n", cantidad, p.nombre(), subtotal);
            }

            if (detalles.isEmpty()) {
                System.out.println("Compra cancelada: no se agregaron productos.");
                return;
            }

            int idCompra = compraDao.registrarCompra(idProveedor, detalles);
            System.out.printf("Compra #%d registrada con %d producto(s). Total: $%s%n",
                    idCompra, detalles.size(), total);
        } catch (SQLException e) {
            if (ErroresSql.es(e, ErroresSql.FK_VIOLADA)) {
                System.out.println("El proveedor o algún producto ya no existe.");
            } else {
                System.out.println("No se pudo registrar la compra: " + e.getMessage());
            }
            System.out.println("La compra no se guardó: se revirtieron todos los cambios.");
        }
    }

    private void listarCompras() {
        try {
            Tablas.imprimirCompras(compraDao.listar());
        } catch (SQLException e) {
            System.out.println("No se pudo listar las compras: " + e.getMessage());
        }
    }

    private void verDetalleCompra() {
        int idCompra = consola.leerEntero("ID de la compra: ");

        try {
            List<DetalleCompra> detalles = compraDao.detallesDe(idCompra);
            if (detalles.isEmpty()) {
                System.out.println("No existe la compra " + idCompra + " o no tiene productos.");
                return;
            }
            BigDecimal total = Tablas.imprimirDetallesCompra(detalles);
            System.out.printf("Total de la compra: $%s%n", total);
        } catch (SQLException e) {
            System.out.println("No se pudo consultar la compra: " + e.getMessage());
        }
    }
}
