package com.proyecto;

import java.sql.Connection;
import java.sql.DriverManager;

public class Main {
    public static void main(String[] args) {

        String url = System.getenv("DB_URL");
        String usuario = System.getenv("DB_USER");
        String contrasena = System.getenv("DB_PASSWORD");

        try (Connection conn = DriverManager.getConnection(url, usuario, contrasena)) {
            System.out.println("Conexión exitosa");
        } catch (Exception e) {
            System.out.println("Error al conectar");
            e.printStackTrace();
        }

    }
}