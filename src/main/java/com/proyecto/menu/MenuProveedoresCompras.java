package com.proyecto.menu;

import com.proyecto.Consola;
import com.proyecto.dao.CompraDao;
import com.proyecto.dao.ProveedorDao;

/**
 * Submenú de proveedores y compras.
 * El menú ya está armado; cada opción está pendiente (ver TODO).
 */
public class MenuProveedoresCompras {

    private final Consola consola;
    private final ProveedorDao proveedorDao;
    private final CompraDao compraDao;

    public MenuProveedoresCompras(Consola consola, ProveedorDao proveedorDao, CompraDao compraDao) {
        this.consola = consola;
        this.proveedorDao = proveedorDao;
        this.compraDao = compraDao;
    }

    public void ejecutar() {
        while (true) {
            System.out.println();
            System.out.println("===== GESTIÓN DE PROVEEDORES Y COMPRAS =====");
            System.out.println("1. Listar proveedores");
            System.out.println("2. Agregar proveedor");
            System.out.println("3. Registrar compra");
            System.out.println("4. Listar compras");
            System.out.println("5. Volver al menú principal");

            switch (consola.leerEntero("Elige una opción: ")) {
                // TODO: llamar a proveedorDao.listar() e imprimir cada proveedor.
                case 1 -> System.out.println("Próximamente: listar proveedores.");
                // TODO: pedir nombre y al menos correo o teléfono; llamar a proveedorDao.agregar().
                case 2 -> System.out.println("Próximamente: agregar proveedor.");
                // TODO: pedir id del proveedor y, en un ciclo, SKU + cantidad + costo de cada producto;
                //       luego compraDao.registrarCompra() (transacción que suma existencias).
                case 3 -> System.out.println("Próximamente: registrar compra.");
                // TODO: llamar a compraDao.listar() y mostrar fecha, proveedor y total.
                case 4 -> System.out.println("Próximamente: listar compras.");
                case 5 -> {
                    return;
                }
                default -> System.out.println("Opción no válida. Elige un número del 1 al 5.");
            }
        }
    }
}
