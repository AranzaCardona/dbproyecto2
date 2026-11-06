package com.proyecto.modelo;

import java.time.OffsetDateTime;

/** Una fila de la tabla ventas (TIMESTAMP WITH TIME ZONE se mapea a OffsetDateTime). */
public record Venta(int idVenta, OffsetDateTime fechaVenta, int idCliente) {
}
