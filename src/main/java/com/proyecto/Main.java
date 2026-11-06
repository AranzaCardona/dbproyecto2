package com.proyecto;

import com.proyecto.dao.CategoriaDao;
import com.proyecto.dao.ClienteDao;
import com.proyecto.dao.CompraDao;
import com.proyecto.dao.ProductoDao;
import com.proyecto.dao.ProveedorDao;
import com.proyecto.dao.VentaDao;
import com.proyecto.menu.MenuClientesVentas;
import com.proyecto.menu.MenuPrincipal;
import com.proyecto.menu.MenuProductos;
import com.proyecto.menu.MenuProveedoresCompras;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Punto de entrada: abre la conexión, arma los objetos y lanza el menú principal.
 * No contiene lógica de negocio ni SQL.
 */
public class Main {

    public static void main(String[] args) {
        // Una sola conexión para toda la sesión; try-with-resources la cierra al salir.
        try (Connection conn = BaseDatos.conectar()) {
            System.out.println("Conexión exitosa.");

            // Un solo Scanner (dentro de Consola) compartido por todos los menús.
            Consola consola = new Consola();

            // Los DAO comparten la misma conexión.
            ProductoDao productoDao = new ProductoDao(conn);
            CategoriaDao categoriaDao = new CategoriaDao(conn);
            ClienteDao clienteDao = new ClienteDao(conn);
            VentaDao ventaDao = new VentaDao(conn);
            ProveedorDao proveedorDao = new ProveedorDao(conn);
            CompraDao compraDao = new CompraDao(conn);

            MenuProductos menuProductos = new MenuProductos(consola, productoDao, categoriaDao);
            MenuClientesVentas menuClientesVentas =
                    new MenuClientesVentas(consola, clienteDao, ventaDao, productoDao);
            MenuProveedoresCompras menuProveedoresCompras =
                    new MenuProveedoresCompras(consola, proveedorDao, compraDao, productoDao);

            new MenuPrincipal(consola, menuProductos, menuClientesVentas, menuProveedoresCompras)
                    .ejecutar();
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("No se pudo conectar a la base de datos: " + e.getMessage());
        }
    }
}
