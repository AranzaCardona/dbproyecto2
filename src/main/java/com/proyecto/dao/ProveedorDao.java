package com.proyecto.dao;

import java.sql.Connection;

/**
 * Acceso a la tabla proveedores.
 *
 * TODO (pendiente de implementar):
 *  - List<Proveedor> listar()
 *  - int agregar(String nombre, String correo, String telefono)
 *      El esquema exige correo o teléfono (al menos uno): si faltan ambos, falla el CHECK (23514).
 *      Ambos son UNIQUE: un duplicado da 23505.
 *  - int actualizar(...) / int eliminar(int idProveedor)
 *      Un proveedor con compras no se puede eliminar (llave foránea, 23503).
 */
public class ProveedorDao {

    private final Connection conn;

    public ProveedorDao(Connection conn) {
        this.conn = conn;
    }
}
