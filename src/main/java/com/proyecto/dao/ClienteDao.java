package com.proyecto.dao;

import com.proyecto.modelo.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Acceso a la tabla clientes.
 * correo_cliente y telefono_cliente son opcionales (NULL) pero UNIQUE (duplicado = 23505).
 */
public class ClienteDao {

    // TODO SQL: listar todos los clientes.
    // Columnas que lee Java: id_cliente, nombre_cliente, correo_cliente, telefono_cliente
    private static final String SQL_LISTAR = """
            """;

    // TODO SQL: buscar UN cliente por su id.
    // Parámetros: 1 = id_cliente
    // Columnas que lee Java: id_cliente, nombre_cliente, correo_cliente, telefono_cliente
    private static final String SQL_BUSCAR_POR_ID = """
            """;

    // TODO SQL: insertar un cliente.
    // Parámetros: 1 = nombre_cliente, 2 = correo_cliente, 3 = telefono_cliente
    private static final String SQL_INSERTAR = """
            """;

    // TODO SQL: actualizar nombre, correo y teléfono de un cliente.
    // Parámetros: 1 = nombre_cliente, 2 = correo_cliente, 3 = telefono_cliente, 4 = id_cliente
    private static final String SQL_ACTUALIZAR = """
            """;

    // TODO SQL: eliminar un cliente por su id (sin cascada).
    // Parámetros: 1 = id_cliente
    // Si tiene ventas, PostgreSQL rechaza el DELETE por llave foránea (23503).
    private static final String SQL_ELIMINAR = """
            """;

    private final Connection conn;

    public ClienteDao(Connection conn) {
        this.conn = conn;
    }

    public List<Cliente> listar() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        try (PreparedStatement ps = JdbcUtil.preparar(conn, SQL_LISTAR, "ClienteDao.SQL_LISTAR");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                clientes.add(mapear(rs));
            }
        }
        return clientes;
    }

    public Optional<Cliente> buscarPorId(int idCliente) throws SQLException {
        try (PreparedStatement ps = JdbcUtil.preparar(conn, SQL_BUSCAR_POR_ID, "ClienteDao.SQL_BUSCAR_POR_ID")) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    /** correo y telefono pueden ser null. @return filas insertadas. */
    public int agregar(String nombre, String correo, String telefono) throws SQLException {
        try (PreparedStatement ps = JdbcUtil.preparar(conn, SQL_INSERTAR, "ClienteDao.SQL_INSERTAR")) {
            ps.setString(1, nombre);
            JdbcUtil.setTextoOpcional(ps, 2, correo);
            JdbcUtil.setTextoOpcional(ps, 3, telefono);
            return ps.executeUpdate();
        }
    }

    /** @return filas actualizadas (0 si el id no existe). */
    public int actualizar(int idCliente, String nombre, String correo, String telefono) throws SQLException {
        try (PreparedStatement ps = JdbcUtil.preparar(conn, SQL_ACTUALIZAR, "ClienteDao.SQL_ACTUALIZAR")) {
            ps.setString(1, nombre);
            JdbcUtil.setTextoOpcional(ps, 2, correo);
            JdbcUtil.setTextoOpcional(ps, 3, telefono);
            ps.setInt(4, idCliente);
            return ps.executeUpdate();
        }
    }

    /** @return filas eliminadas (0 si el id no existe). */
    public int eliminar(int idCliente) throws SQLException {
        try (PreparedStatement ps = JdbcUtil.preparar(conn, SQL_ELIMINAR, "ClienteDao.SQL_ELIMINAR")) {
            ps.setInt(1, idCliente);
            return ps.executeUpdate();
        }
    }

    private static Cliente mapear(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id_cliente"),
                rs.getString("nombre_cliente"),
                rs.getString("correo_cliente"),
                rs.getString("telefono_cliente"));
    }
}
