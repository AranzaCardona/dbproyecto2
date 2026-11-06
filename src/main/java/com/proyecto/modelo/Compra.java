package com.proyecto.modelo;

import java.time.OffsetDateTime;

/** Una fila de la tabla compras (TIMESTAMP WITH TIME ZONE se mapea a OffsetDateTime). */
public record Compra(int idCompra, OffsetDateTime fechaCompra, int idProveedor) {
}
