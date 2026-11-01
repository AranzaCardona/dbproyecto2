-- Datos de ejemplo para probar el esquema de inventario y ventas.


BEGIN;

INSERT INTO categorias (nombre_categoria)
VALUES ('Bebidas'), ('Abarrotes')
ON CONFLICT (nombre_categoria) DO NOTHING;

INSERT INTO productos (sku, nombre, precio, existencias, id_categoria)
SELECT 'BEB-001', 'Agua mineral 600 ml', 12.50, 17, id_categoria
FROM categorias
WHERE nombre_categoria = 'Bebidas'
ON CONFLICT (sku) DO NOTHING;

INSERT INTO productos (sku, nombre, precio, existencias, id_categoria)
SELECT 'ABA-001', 'Cafe molido 250 g', 68.00, 9, id_categoria
FROM categorias
WHERE nombre_categoria = 'Abarrotes'
ON CONFLICT (sku) DO NOTHING;

INSERT INTO clientes (nombre_cliente, correo_cliente, telefono_cliente)
VALUES ('Ana Lopez', 'ana.lopez@example.test', '9511234567')
ON CONFLICT (correo_cliente) DO NOTHING;

-- Este proveedor muestra que el correo puede ser el unico dato de contacto.
INSERT INTO proveedores (nombre_proveedor, correo_proveedor)
VALUES ('Distribuidora Mixteca', 'ventas@distribuidora.example.test')
ON CONFLICT (correo_proveedor) DO NOTHING;

-- Este proveedor muestra que tambien se acepta registrar solo el telefono.
INSERT INTO proveedores (nombre_proveedor, telefono_proveedor)
VALUES ('Abarrotes del Valle', '9517654321')
ON CONFLICT (telefono_proveedor) DO NOTHING;

WITH compra_demo AS (
    INSERT INTO compras (id_proveedor)
    SELECT id_proveedor
    FROM proveedores
    WHERE correo_proveedor = 'ventas@distribuidora.example.test'
    RETURNING id_compra
)
INSERT INTO detalle_compra (id_compra, id_producto, cantidad, costo_unitario)
SELECT compra_demo.id_compra, productos.id_producto, detalle.cantidad, detalle.costo_unitario
FROM compra_demo
CROSS JOIN (VALUES
    ('BEB-001', 20, 8.00),
    ('ABA-001', 10, 48.00)
) AS detalle(sku, cantidad, costo_unitario)
JOIN productos ON productos.sku = detalle.sku;

WITH venta_demo AS (
    INSERT INTO ventas (id_cliente)
    SELECT id_cliente
    FROM clientes
    WHERE correo_cliente = 'ana.lopez@example.test'
    RETURNING id_venta
)
INSERT INTO detalle_venta (id_venta, id_producto, cantidad, precio_unitario)
SELECT venta_demo.id_venta, productos.id_producto, detalle.cantidad, detalle.precio_unitario
FROM venta_demo
CROSS JOIN (VALUES
    ('BEB-001', 3, 12.50),
    ('ABA-001', 1, 68.00)
) AS detalle(sku, cantidad, precio_unitario)
JOIN productos ON productos.sku = detalle.sku;

COMMIT;
