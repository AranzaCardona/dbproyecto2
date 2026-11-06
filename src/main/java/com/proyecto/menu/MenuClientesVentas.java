package com.proyecto.menu;

import com.proyecto.Consola;
import com.proyecto.dao.ClienteDao;
import com.proyecto.dao.VentaDao;

/**
 * Submenú de clientes y ventas.
 * El menú ya está armado; cada opción está pendiente (ver TODO).
 */
public class MenuClientesVentas {

    private final Consola consola;
    private final ClienteDao clienteDao;
    private final VentaDao ventaDao;

    public MenuClientesVentas(Consola consola, ClienteDao clienteDao, VentaDao ventaDao) {
        this.consola = consola;
        this.clienteDao = clienteDao;
        this.ventaDao = ventaDao;
    }

    public void ejecutar() {
        while (true) {
            System.out.println();
            System.out.println("===== GESTIÓN DE CLIENTES Y VENTAS =====");
            System.out.println("1. Listar clientes");
            System.out.println("2. Agregar cliente");
            System.out.println("3. Registrar venta");
            System.out.println("4. Listar ventas");
            System.out.println("5. Volver al menú principal");

            switch (consola.leerEntero("Elige una opción: ")) {
                // TODO: llamar a clienteDao.listar() e imprimir cada cliente.
                case 1 -> System.out.println("Próximamente: listar clientes.");
                // TODO: pedir nombre (obligatorio), correo y teléfono (opcionales) y llamar a clienteDao.agregar().
                case 2 -> System.out.println("Próximamente: agregar cliente.");
                // TODO: pedir id del cliente y, en un ciclo, SKU + cantidad de cada producto;
                //       luego ventaDao.registrarVenta() (transacción que descuenta existencias).
                case 3 -> System.out.println("Próximamente: registrar venta.");
                // TODO: llamar a ventaDao.listar() y mostrar fecha, cliente y total.
                case 4 -> System.out.println("Próximamente: listar ventas.");
                case 5 -> {
                    return;
                }
                default -> System.out.println("Opción no válida. Elige un número del 1 al 5.");
            }
        }
    }
}
