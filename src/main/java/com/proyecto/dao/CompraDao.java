package com.proyecto.dao;

import java.sql.Connection;

/**
 * Acceso a las tablas compras y detalle_compra.
 *
 * TODO (pendiente de implementar):
 *  - int registrarCompra(int idProveedor, List<DetalleCompra> detalles)
 *      Debe ser UNA TRANSACCIÓN (todo o nada):
 *        1. conn.setAutoCommit(false)
 *        2. INSERT INTO compras (id_proveedor) VALUES (?) RETURNING id_compra;
 *        3. Por cada detalle: INSERT INTO detalle_compra (...) VALUES (?, ?, ?, ?);
 *        4. Sumar inventario:
 *             UPDATE productos SET existencias = existencias + ? WHERE id_producto = ?;
 *        5. conn.commit(); ante cualquier error conn.rollback().
 *        6. En finally: conn.setAutoCommit(true).
 *  - List<Compra> listar() y List<DetalleCompra> detallesDe(int idCompra)
 */
public class CompraDao {

    private final Connection conn;

    public CompraDao(Connection conn) {
        this.conn = conn;
    }
}
