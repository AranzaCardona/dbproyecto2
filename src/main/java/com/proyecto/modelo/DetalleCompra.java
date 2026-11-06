package com.proyecto.modelo;

import java.math.BigDecimal;

/** Una fila de detalle_compra: qué producto, cuántos y a qué costo se compró. */
public record DetalleCompra(int idCompra, int idProducto, int cantidad, BigDecimal costoUnitario) {
}
