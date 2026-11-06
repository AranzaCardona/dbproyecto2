package com.proyecto.dao;

import java.sql.Connection;

/**
 * Acceso a la tabla categorias.
 *
 * TODO (pendiente de implementar):
 *  - List<Categoria> listar()
 *      SELECT id_categoria, nombre_categoria FROM categorias ORDER BY nombre_categoria;
 *  - int agregar(String nombreCategoria)
 *      INSERT INTO categorias (nombre_categoria) VALUES (?);
 *      nombre_categoria es UNIQUE y varchar(30): un duplicado da SQLSTATE 23505.
 *  - Eliminar una categoría con productos fallará por llave foránea (23503); mostrar mensaje claro.
 */
public class CategoriaDao {

    private final Connection conn;

    public CategoriaDao(Connection conn) {
        this.conn = conn;
    }
}
