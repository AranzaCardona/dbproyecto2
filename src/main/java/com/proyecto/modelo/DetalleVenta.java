package com.proyecto.modelo;

import java.math.BigDecimal;

/** Una fila de detalle_venta: qué producto, cuántos y a qué precio se vendió. */
public record DetalleVenta(int idVenta, int idProducto, int cantidad, BigDecimal precioUnitario) {
}
