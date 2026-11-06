package com.proyecto.menu;

import com.proyecto.Consola;
import com.proyecto.ErroresSql;
import com.proyecto.dao.CategoriaDao;
import com.proyecto.dao.ProductoDao;

import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * Submenú de productos y categorías: pide datos, llama a los DAO y muestra resultados.
 * Aquí NO hay SQL; los errores SQL se traducen a mensajes comprensibles.
 */
public class MenuProductos {

    private final Consola consola;
    private final ProductoDao productoDao;
    private final CategoriaDao categoriaDao;

    public MenuProductos(Consola consola, ProductoDao productoDao, CategoriaDao categoriaDao) {
        this.consola = consola;
        this.productoDao = productoDao;
        this.categoriaDao = categoriaDao;
    }

    public void ejecutar() {
        while (true) {
            System.out.println();
            System.out.println("===== GESTIÓN DE PRODUCTOS Y CATEGORÍAS =====");
            System.out.println("1. Listar productos");
            System.out.println("2. Agregar producto");
            System.out.println("3. Actualizar precio y existencias (por SKU)");
            System.out.println("4. Eliminar producto (por SKU)");
            System.out.println("5. Listar categorías");
            System.out.println("6. Agregar categoría");
            System.out.println("7. Eliminar categoría");
            System.out.println("8. Volver al menú principal");

            switch (consola.leerEntero("Elige una opción: ")) {
                case 1 -> listarProductos();
                case 2 -> agregarProducto();
                case 3 -> actualizarProducto();
                case 4 -> eliminarProducto();
                case 5 -> listarCategorias();
                case 6 -> agregarCategoria();
                case 7 -> eliminarCategoria();
                case 8 -> {
                    return;
                }
                default -> System.out.println("Opción no válida. Elige un número del 1 al 8.");
            }
        }
    }

    /** Usado por "Imprimir todas las tablas" del menú principal. */
    public void imprimirTablas() {
        Tablas.titulo("categorias");
        listarCategorias();
        Tablas.titulo("productos");
        listarProductos();
    }

    // ---------- Productos ----------

    private void listarProductos() {
        try {
            Tablas.imprimirProductos(productoDao.listar());
        } catch (SQLException e) {
            System.out.println("No se pudo listar los productos: " + e.getMessage());
        }
    }

    private void agregarProducto() {
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

    private void actualizarProducto() {
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

    private void eliminarProducto() {
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

    // ---------- Categorías ----------

    private void listarCategorias() {
        try {
            Tablas.imprimirCategorias(categoriaDao.listar());
        } catch (SQLException e) {
            System.out.println("No se pudo listar las categorías: " + e.getMessage());
        }
    }

    private void agregarCategoria() {
        String nombre = consola.leerTexto("Nombre de la categoría (máx. 30 caracteres): ", 30);

        try {
            int filas = categoriaDao.agregar(nombre);
            System.out.printf("Categoría agregada. Filas afectadas: %d%n", filas);
        } catch (SQLException e) {
            if (ErroresSql.es(e, ErroresSql.UNICO_VIOLADO)) {
                System.out.println("Ya existe una categoría llamada '" + nombre + "'.");
            } else {
                System.out.println("No se pudo agregar la categoría: " + e.getMessage());
            }
        }
    }

    private void eliminarCategoria() {
        int idCategoria = consola.leerEntero("ID de la categoría a eliminar: ");

        try {
            int filas = categoriaDao.eliminar(idCategoria);
            if (filas == 0) {
                System.out.println("No existe ninguna categoría con ID " + idCategoria + ". Filas afectadas: 0");
            } else {
                System.out.printf("Categoría eliminada. Filas afectadas: %d%n", filas);
            }
        } catch (SQLException e) {
            if (ErroresSql.es(e, ErroresSql.FK_VIOLADA)) {
                System.out.println("No se puede eliminar la categoría " + idCategoria
                        + ": tiene productos asignados. Filas afectadas: 0");
            } else {
                System.out.println("No se pudo eliminar la categoría: " + e.getMessage());
            }
        }
    }
}
