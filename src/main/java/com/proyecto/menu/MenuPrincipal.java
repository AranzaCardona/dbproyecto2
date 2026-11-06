package com.proyecto.menu;

import com.proyecto.Consola;

/**
 * Menú principal de la tienda. Solo decide a qué submenú entrar.
 * Cada submenú tiene su propio ciclo y regresa aquí al elegir "Volver".
 */
public class MenuPrincipal {

    private final Consola consola;
    private final MenuProductos menuProductos;
    private final MenuClientesVentas menuClientesVentas;
    private final MenuProveedoresCompras menuProveedoresCompras;

    public MenuPrincipal(Consola consola,
                         MenuProductos menuProductos,
                         MenuClientesVentas menuClientesVentas,
                         MenuProveedoresCompras menuProveedoresCompras) {
        this.consola = consola;
        this.menuProductos = menuProductos;
        this.menuClientesVentas = menuClientesVentas;
        this.menuProveedoresCompras = menuProveedoresCompras;
    }

    public void ejecutar() {
        while (true) {
            System.out.println();
            System.out.println("===== GESTIÓN DE LA TIENDA MINISUPER UTEMITA =====");
            System.out.println("1. Gestionar productos y categorias de productos");
            System.out.println("2. Gestionar clientes y ventas");
            System.out.println("3. Gestionar proveedores y compras");
            System.out.println("4. Imprimir todas las tablas");
            System.out.println("5. Salir");

            switch (consola.leerEntero("Elige una opción: ")) {
                case 1 -> menuProductos.ejecutar();
                case 2 -> menuClientesVentas.ejecutar();
                case 3 -> menuProveedoresCompras.ejecutar();
                case 4 -> imprimirTodasLasTablas();
                case 5 -> {
                    System.out.println("¡Hasta luego!");
                    return;
                }
                default -> System.out.println("Opción no válida. Elige un número del 1 al 5.");
            }
        }
    }

    /**
     * Cada submenú imprime sus propias tablas. Si una consulta falla (por ejemplo,
     * porque su SQL aún no está escrito), se muestra el error y se sigue con las demás.
     */
    private void imprimirTodasLasTablas() {
        System.out.println();
        System.out.println("===== TODAS LAS TABLAS =====");
        menuProductos.imprimirTablas();
        menuClientesVentas.imprimirTablas();
        menuProveedoresCompras.imprimirTablas();
    }
}
