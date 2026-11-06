package com.proyecto;

import java.sql.SQLException;

/**
 * Códigos SQLSTATE de PostgreSQL que la aplicación traduce a mensajes comprensibles.
 * Lista completa: https://www.postgresql.org/docs/current/errcodes-appendix.html
 */
public final class ErroresSql {

    /** Se viola una llave foránea (referencia inexistente o fila con dependientes). */
    public static final String FK_VIOLADA = "23503";
    /** Se repite un valor en una columna UNIQUE (por ejemplo, el SKU). */
    public static final String UNICO_VIOLADO = "23505";
    /** No se cumple un CHECK (por ejemplo, precio >= 0). */
    public static final String CHECK_VIOLADO = "23514";

    private ErroresSql() {
        // Clase de utilidad: no se instancia.
    }

    /** Devuelve true si la excepción tiene el código indicado. */
    public static boolean es(SQLException e, String codigo) {
        return codigo.equals(e.getSQLState());
    }
}
