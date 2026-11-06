package com.proyecto.modelo;

/** Una fila de la tabla clientes. Correo y teléfono pueden ser null. */
public record Cliente(int idCliente, String nombreCliente, String correoCliente, String telefonoCliente) {
}
