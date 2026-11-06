package com.proyecto;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    // Un solo Scanner para toda la aplicación (evita varios Scanner sobre System.in).
    private static final Scanner TECLADO = new Scanner(System.in);

    // Códigos SQLSTATE de PostgreSQL que nos interesan.
    private static final String FK_VIOLADA = "23503";
    private static final String UNICO_VIOLADO = "23505";
    private static final String CHECK_VIOLADO = "23514";

    private static final String SQL_LISTAR = """
            SELECT p.id_producto, p.sku, p.nombre, p.precio, p.existencias,
                   c.nombre_categoria
            FROM productos p
            JOIN categorias c ON p.id_categoria = c.id_categoria
            ORDER BY p.nombre;
            """;

    private static final String SQL_INSERTAR = """
            INSERT INTO productos (sku, nombre, precio, existencias, id_categoria)
            VALUES (?, ?, ?, ?, ?);
            """;

    private static final String SQL_ACTUALIZAR = """
            UPDATE productos SET precio = ?, existencias = ? WHERE sku = ?;
            """;

    private static final String SQL_ELIMINAR = """
            DELETE FROM productos WHERE sku = ?;
            """;

    public static void main(String[] args) {
        String url = System.getenv("DB_URL");
        String usuario = System.getenv("DB_USER");
        String contrasena = System.getenv("DB_PASSWORD");

        if (url == null || usuario == null || contrasena == null) {
            System.out.println("Faltan variables de entorno: define DB_URL, DB_USER y DB_PASSWORD.");
            return;
        }

        // Una sola conexión durante toda la sesión del menú.
        try (Connection conn = DriverManager.getConnection(url, usuario, contrasena)) {
            System.out.println("Conexión exitosa.");
            boolean salir = false;
            while (!salir) {
                mostrarMenu();
                int opcion = leerEntero("Elige una opción: ");
                // Cada operación maneja sus propios errores SQL, así el menú sigue vivo.
                switch (opcion) {
                    case 1 -> listarProductos(conn);
                    case 2 -> agregarProducto(conn);
                    case 3 -> actualizarProducto(conn);
                    case 4 -> eliminarProducto(conn);
                    case 5 -> {
                        System.out.println("¡Hasta luego!");
                        salir = true;
                    }
                    default -> System.out.println("Opción no válida. Elige un número del 1 al 5.");
                }
            }
        } catch (SQLException e) {
            System.out.println("No se pudo conectar a la base de datos: " + e.getMessage());
        }
    }

    // ---------- Menú ----------

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("===== GESTIÓN DE PRODUCTOS =====");
        System.out.println("1. Listar productos");
        System.out.println("2. Agregar producto");
        System.out.println("3. Actualizar precio y existencias (por SKU)");
        System.out.println("4. Eliminar producto (por SKU)");
        System.out.println("5. Salir");
    }

    // ---------- Operaciones ----------

    private static void listarProductos(Connection conn) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            int total = 0;
            while (rs.next()) {
                System.out.printf("%d | %s | %s | $%s | existencias: %d | categoría: %s%n",
                        rs.getInt("id_producto"),
                        rs.getString("sku"),
                        rs.getString("nombre"),
                        rs.getBigDecimal("precio"),
                        rs.getInt("existencias"),
                        rs.getString("nombre_categoria"));
                total++;
            }
            if (total == 0) {
                System.out.println("No hay productos registrados.");
            } else {
                System.out.printf("Total: %d producto(s).%n", total);
            }
        } catch (SQLException e) {
            System.out.println("No se pudo listar los productos: " + e.getMessage());
        }
    }

    private static void agregarProducto(Connection conn) {
        String sku = leerTexto("SKU (máx. 20 caracteres): ", 20);
        String nombre = leerTexto("Nombre (máx. 50 caracteres): ", 50);
        BigDecimal precio = leerDecimalNoNegativo("Precio: ");
        int existencias = leerEnteroNoNegativo("Existencias: ");
        int idCategoria = leerEntero("ID de la categoría: ");

        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERTAR)) {
            ps.setString(1, sku);
            ps.setString(2, nombre);
            ps.setBigDecimal(3, precio);
            ps.setInt(4, existencias);
            ps.setInt(5, idCategoria);
            int filas = ps.executeUpdate();
            System.out.printf("Producto agregado. Filas afectadas: %d%n", filas);
        } catch (SQLException e) {
            switch (e.getSQLState() == null ? "" : e.getSQLState()) {
                case UNICO_VIOLADO -> System.out.println("Ya existe un producto con el SKU '" + sku + "'.");
                case FK_VIOLADA -> System.out.println("La categoría con ID " + idCategoria + " no existe.");
                case CHECK_VIOLADO -> System.out.println("Datos fuera de rango: precio y existencias no pueden ser negativos.");
                default -> System.out.println("No se pudo agregar el producto: " + e.getMessage());
            }
        }
    }

    private static void actualizarProducto(Connection conn) {
        String sku = leerTexto("SKU del producto a actualizar: ", 20);
        BigDecimal precio = leerDecimalNoNegativo("Nuevo precio: ");
        int existencias = leerEnteroNoNegativo("Nuevas existencias: ");

        try (PreparedStatement ps = conn.prepareStatement(SQL_ACTUALIZAR)) {
            ps.setBigDecimal(1, precio);
            ps.setInt(2, existencias);
            ps.setString(3, sku);
            int filas = ps.executeUpdate();
            if (filas == 0) {
                System.out.println("No existe ningún producto con el SKU '" + sku + "'. Filas afectadas: 0");
            } else {
                System.out.printf("Producto actualizado. Filas afectadas: %d%n", filas);
            }
        } catch (SQLException e) {
            if (CHECK_VIOLADO.equals(e.getSQLState())) {
                System.out.println("Datos fuera de rango: precio y existencias no pueden ser negativos.");
            } else {
                System.out.println("No se pudo actualizar el producto: " + e.getMessage());
            }
        }
    }

    private static void eliminarProducto(Connection conn) {
        String sku = leerTexto("SKU del producto a eliminar: ", 20);

        // Sin ON DELETE CASCADE: si detalle_venta o detalle_compra referencian al producto,
        // PostgreSQL rechaza el DELETE (SQLSTATE 23503) y el historial queda intacto.
        try (PreparedStatement ps = conn.prepareStatement(SQL_ELIMINAR)) {
            ps.setString(1, sku);
            int filas = ps.executeUpdate();
            if (filas == 0) {
                System.out.println("No existe ningún producto con el SKU '" + sku + "'. Filas afectadas: 0");
            } else {
                System.out.printf("Producto eliminado. Filas afectadas: %d%n", filas);
            }
        } catch (SQLException e) {
            if (FK_VIOLADA.equals(e.getSQLState())) {
                System.out.println("No se puede eliminar '" + sku + "': tiene compras o ventas relacionadas.");
                System.out.println("Se conserva el historial; el producto no fue borrado. Filas afectadas: 0");
            } else {
                System.out.println("No se pudo eliminar el producto: " + e.getMessage());
            }
        }
    }

    // ---------- Lectura de consola (todo con nextLine sobre el mismo Scanner) ----------

    private static String leerTexto(String mensaje, int maxLongitud) {
        while (true) {
            System.out.print(mensaje);
            String valor = TECLADO.nextLine().trim();
            if (valor.isEmpty()) {
                System.out.println("El valor no puede estar vacío.");
            } else if (valor.length() > maxLongitud) {
                System.out.println("Máximo " + maxLongitud + " caracteres.");
            } else {
                return valor;
            }
        }
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(TECLADO.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida: escribe un número entero.");
            }
        }
    }

    private static int leerEnteroNoNegativo(String mensaje) {
        while (true) {
            int valor = leerEntero(mensaje);
            if (valor >= 0) {
                return valor;
            }
            System.out.println("El valor no puede ser negativo.");
        }
    }

    private static BigDecimal leerDecimalNoNegativo(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                BigDecimal valor = new BigDecimal(TECLADO.nextLine().trim());
                if (valor.signum() < 0) {
                    System.out.println("El valor no puede ser negativo.");
                } else if (valor.scale() > 2 || valor.precision() - valor.scale() > 8) {
                    System.out.println("Usa máximo 2 decimales y 8 dígitos enteros (ej. 19.99).");
                } else {
                    return valor;
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida: escribe un número, por ejemplo 19.99.");
            }
        }
    }
}
