package com.proyecto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Crea la conexión a PostgreSQL usando variables de entorno.
 * La contraseña nunca se imprime ni se guarda en el código.
 */
public final class BaseDatos {

    private BaseDatos() {
        // Clase de utilidad: no se instancia.
    }

    public static Connection conectar() throws SQLException {
        String url = System.getenv("DB_URL");
        String usuario = System.getenv("DB_USER");
        String contrasena = System.getenv("DB_PASSWORD");

        if (url == null || usuario == null || contrasena == null) {
            throw new IllegalStateException(
                    "Faltan variables de entorno: define DB_URL, DB_USER y DB_PASSWORD.");
        }
        return DriverManager.getConnection(url, usuario, contrasena);
    }
}
