package com.proyecto.modelo;

/**
 * Una fila de la tabla proveedores.
 * El esquema exige que correo o teléfono (al menos uno) no sea null.
 */
public record Proveedor(int idProveedor, String nombreProveedor, String correoProveedor, String telefonoProveedor) {
}
