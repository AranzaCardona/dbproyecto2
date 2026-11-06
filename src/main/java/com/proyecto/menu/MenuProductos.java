package com.proyecto.menu;

import com.proyecto.Consola;
import com.proyecto.ErroresSql;
import com.proyecto.dao.ProductoDao;
import com.proyecto.modelo.Producto;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Submenú de productos: pide datos, llama al DAO y muestra resultados.
 * Aquí NO hay SQL; los errores SQL se traducen a mensajes comprensibles.
 */
public class MenuProductos {

    private final Consola consola;
    private final ProductoDao productoDao;

    public MenuProductos(Consola consola, ProductoDao productoDao) {
        this.consola = consola;
        this.productoDao = productoDao;
    }

    public void ejecutar() {
        while (true) {
            System.out.println();
            System.out.println("===== GESTIÓN DE PRODUCTOS =====");
            System.out.println("1. Listar productos");
            System.out.println("2. Agregar producto");
            System.out.println("3. Actualizar precio y existencias (por SKU)");
            System.out.println("4. Eliminar producto (por SKU)");
            System.out.println("5. Listar categorías");
            System.out.println("6. Agregar categoría");
            System.out.println("7. Volver al menú principal");

            switch (consola.leerEntero("Elige una opción: ")) {
                case 1 -> listar();
                case 2 -> agregar();
                case 3 -> actualizar();
                case 4 -> eliminar();
                // TODO: implementar con CategoriaDao (recibirlo en el constructor desde Main).
                case 5, 6 -> System.out.println("Próximamente: gestión de categorías.");
                case 7 -> {
                    return;
                }
                default -> System.out.println("Opción no válida. Elige un número del 1 al 7.");
            }
        }
    }

    private void listar() {
        try {
            List<Producto> productos = productoDao.listar();
            if (productos.isEmpty()) {
                System.out.println("No hay productos registrados.");
                return;
            }
            for (Producto p : productos) {
                System.out.printf("%d | %s | %s | $%s | existencias: %d | categoría: %s%n",
                        p.idProducto(), p.sku(), p.nombre(), p.precio(),
                        p.existencias(), p.nombreCategoria());
            }
            System.out.printf("Total: %d producto(s).%n", productos.size());
        } catch (SQLException e) {
            System.out.println("No se pudo listar los productos: " + e.getMessage());
        }
    }

    private void agregar() {
        String sku = consola.leerTexto("SKU (máx. 20 caracteres): ", 20);
        String nombre = consola.leerTexto("Nombre (máx. 50 caracteres): ", 50);
        BigDecimal precio = consola.leerDecimalNoNegativo("Precio: ");
        int existencias = consola.leerEnteroNoNegativo("Existencias: ");
        int idCategoria = consola.leerEntero("ID de la categoría: ");

        try {
            int filas = productoDao.agregar(sku, nombre, precio, existencias, idCategoria);
            System.out.printf("Producto agregado. Filas afectadas: %d%n", filas);
        } catch (SQLException e) {
            if (ErroresSql.es(e, ErroresSql.UNICO_VIOLADO)) {
                System.out.println("Ya existe un producto con el SKU '" + sku + "'.");
            } else if (ErroresSql.es(e, ErroresSql.FK_VIOLADA)) {
                System.out.println("La categoría con ID " + idCategoria + " no existe.");
            } else if (ErroresSql.es(e, ErroresSql.CHECK_VIOLADO)) {
                System.out.println("Datos fuera de rango: precio y existencias no pueden ser negativos.");
            } else {
                System.out.println("No se pudo agregar el producto: " + e.getMessage());
            }
        }
    }

    private void actualizar() {
        String sku = consola.leerTexto("SKU del producto a actualizar: ", 20);
        BigDecimal precio = consola.leerDecimalNoNegativo("Nuevo precio: ");
        int existencias = consola.leerEnteroNoNegativo("Nuevas existencias: ");

        try {
            int filas = productoDao.actualizarPrecioYExistencias(sku, precio, existencias);
            if (filas == 0) {
                System.out.println("No existe ningún producto con el SKU '" + sku + "'. Filas afectadas: 0");
            } else {
                System.out.printf("Producto actualizado. Filas afectadas: %d%n", filas);
            }
        } catch (SQLException e) {
            if (ErroresSql.es(e, ErroresSql.CHECK_VIOLADO)) {
                System.out.println("Datos fuera de rango: precio y existencias no pueden ser negativos.");
            } else {
                System.out.println("No se pudo actualizar el producto: " + e.getMessage());
            }
        }
    }

    private void eliminar() {
        String sku = consola.leerTexto("SKU del producto a eliminar: ", 20);

        try {
            int filas = productoDao.eliminar(sku);
            if (filas == 0) {
                System.out.println("No existe ningún producto con el SKU '" + sku + "'. Filas afectadas: 0");
            } else {
                System.out.printf("Producto eliminado. Filas afectadas: %d%n", filas);
            }
        } catch (SQLException e) {
            if (ErroresSql.es(e, ErroresSql.FK_VIOLADA)) {
                System.out.println("No se puede eliminar '" + sku + "': tiene compras o ventas relacionadas.");
                System.out.println("Se conserva el historial; el producto no fue borrado. Filas afectadas: 0");
            } else {
                System.out.println("No se pudo eliminar el producto: " + e.getMessage());
            }
        }
    }
}
