package com.proyecto.modelo;

import java.math.BigDecimal;

/**
 * Un producto de la tabla productos.
 * nombreCategoria viene del JOIN con categorias (no es columna de productos).
 */
public record Producto(
        int idProducto,
        String sku,
        String nombre,
        BigDecimal precio,
        int existencias,
        int idCategoria,
        String nombreCategoria) {
}
