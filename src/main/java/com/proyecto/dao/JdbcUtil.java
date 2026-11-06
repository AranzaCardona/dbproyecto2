package com.proyecto.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Ayudas pequeñas que comparten todos los DAO (solo visibles dentro del paquete dao).
 */
final class JdbcUtil {

    private JdbcUtil() {
        // Clase de utilidad: no se instancia.
    }

    /**
     * Prepara la consulta. Si la constante SQL todavía está vacía, lanza un error
     * que dice exactamente cuál falta escribir, en lugar de un error raro de PostgreSQL.
     */
    static PreparedStatement preparar(Connection conn, String sql, String nombre) throws SQLException {
        if (sql == null || sql.isBlank()) {
            throw new SQLException("Consulta pendiente: escribe el SQL de " + nombre + ".");
        }
        return conn.prepareStatement(sql);
    }

    /** Asigna un texto que puede ser null (columnas opcionales como correo o teléfono). */
    static void setTextoOpcional(PreparedStatement ps, int indice, String valor) throws SQLException {
        if (valor == null) {
            ps.setNull(indice, Types.VARCHAR);
        } else {
            ps.setString(indice, valor);
        }
    }
}
