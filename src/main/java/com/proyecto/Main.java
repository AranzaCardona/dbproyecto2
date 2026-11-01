package com.proyecto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {

        String url = System.getenv("DB_URL");
        String usuario = System.getenv("DB_USER");
        String contrasena = System.getenv("DB_PASSWORD");

        String sql = """
                SELECT p.id_producto, p.sku, p.nombre, p.precio, p.existencias,
                       c.nombre_categoria
                FROM productos p
                JOIN categorias c ON p.id_categoria = c.id_categoria
                ORDER BY p.nombre;
                """;

        try (Connection conn = DriverManager.getConnection(url, usuario, contrasena);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("Conexión exitosa. Productos:");
            while (rs.next()) {
                System.out.printf("%d | %s | %s | $%s | existencias: %d | categoría: %s%n",
                        rs.getInt("id_producto"),
                        rs.getString("sku"),
                        rs.getString("nombre"),
                        rs.getBigDecimal("precio"),
                        rs.getInt("existencias"),
                        rs.getString("nombre_categoria"));
            }
        } catch (SQLException e) {
            System.out.println("Error al conectar o consultar los productos");
            e.printStackTrace();
        }
    }
}