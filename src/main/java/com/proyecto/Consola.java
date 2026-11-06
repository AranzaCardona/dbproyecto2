package com.proyecto;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Lectura de datos desde el teclado.
 * Contiene el ÚNICO Scanner sobre System.in de toda la aplicación.
 * Todo se lee con nextLine() y luego se convierte, para no dejar saltos de línea pendientes.
 */
public class Consola {

    private final Scanner teclado = new Scanner(System.in);

    /** Pide texto no vacío y con longitud máxima (útil para columnas varchar). */
    public String leerTexto(String mensaje, int maxLongitud) {
        while (true) {
            String valor = leerTextoOpcional(mensaje, maxLongitud);
            if (valor != null) {
                return valor;
            }
            System.out.println("El valor no puede estar vacío.");
        }
    }

    /** Pide texto con longitud máxima; si el usuario solo presiona Enter devuelve null. */
    public String leerTextoOpcional(String mensaje, int maxLongitud) {
        while (true) {
            System.out.print(mensaje);
            String valor = teclado.nextLine().trim();
            if (valor.isEmpty()) {
                return null;
            }
            if (valor.length() <= maxLongitud) {
                return valor;
            }
            System.out.println("Máximo " + maxLongitud + " caracteres.");
        }
    }

    public int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(teclado.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida: escribe un número entero.");
            }
        }
    }

    public int leerEnteroNoNegativo(String mensaje) {
        while (true) {
            int valor = leerEntero(mensaje);
            if (valor >= 0) {
                return valor;
            }
            System.out.println("El valor no puede ser negativo.");
        }
    }

    /** Para cantidades de venta/compra: el esquema exige cantidad > 0. */
    public int leerEnteroPositivo(String mensaje) {
        while (true) {
            int valor = leerEntero(mensaje);
            if (valor > 0) {
                return valor;
            }
            System.out.println("El valor debe ser mayor que cero.");
        }
    }

    /** Pide un número compatible con numeric(10,2): máximo 8 enteros y 2 decimales. */
    public BigDecimal leerDecimalNoNegativo(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                BigDecimal valor = new BigDecimal(teclado.nextLine().trim());
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
