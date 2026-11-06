package com.proyecto.dao;

import java.sql.Connection;

/**
 * Acceso a las tablas ventas y detalle_venta.
 *
 * TODO (pendiente de implementar):
 *  - int registrarVenta(int idCliente, List<DetalleVenta> detalles)
 *      Debe ser UNA TRANSACCIÓN (todo o nada):
 *        1. conn.setAutoCommit(false)
 *        2. INSERT INTO ventas (id_cliente) VALUES (?) RETURNING id_venta;
 *        3. Por cada detalle: INSERT INTO detalle_venta (...) VALUES (?, ?, ?, ?);
 *        4. Descontar inventario:
 *             UPDATE productos SET existencias = existencias - ? WHERE id_producto = ?;
 *           Si no hay existencias suficientes, el CHECK (existencias >= 0) falla (23514).
 *        5. conn.commit(); ante cualquier error conn.rollback().
 *        6. En finally: conn.setAutoCommit(true).
 *  - List<Venta> listar() y List<DetalleVenta> detallesDe(int idVenta)
 */
public class VentaDao {

    private final Connection conn;

    public VentaDao(Connection conn) {
        this.conn = conn;
    }
}
