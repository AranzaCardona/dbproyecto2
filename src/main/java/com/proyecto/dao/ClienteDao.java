package com.proyecto.dao;

import java.sql.Connection;

/**
 * Acceso a la tabla clientes.
 *
 * TODO (pendiente de implementar):
 *  - List<Cliente> listar()
 *  - int agregar(String nombre, String correo, String telefono)
 *      correo y teléfono son opcionales (NULL) pero UNIQUE: un duplicado da SQLSTATE 23505.
 *      Usar ps.setNull(i, java.sql.Types.VARCHAR) cuando vengan vacíos.
 *  - int actualizar(...) / int eliminar(int idCliente)
 *      Un cliente con ventas no se puede eliminar (llave foránea, 23503).
 */
public class ClienteDao {

    private final Connection conn;

    public ClienteDao(Connection conn) {
        this.conn = conn;
    }
}
