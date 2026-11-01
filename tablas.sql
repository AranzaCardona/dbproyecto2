CREATE TABLE categorias(id_categoria integer generated always as identity primary key
                        ,nombre_categoria varchar(30) unique not null);

CREATE TABLE productos(id_producto integer generated always as identity primary key 
                        , sku varchar(20) unique not null
                        , precio numeric(10,2) not null check (precio >=0)
                        , nombre  varchar(50) not null
                        , existencias int not null check (existencias >=0)
                        , id_categoria integer not null
                        , foreign key (id_categoria) references categorias(id_categoria));

CREATE TABLE clientes(id_cliente INTEGER generated always as identity primary key,
                      nombre_cliente varchar(50) not null,
                      correo_cliente varchar(50) unique,
                      telefono_cliente varchar(15) unique );

CREATE TABLE ventas(id_venta integer generated always as identity primary key,
                    fecha_venta TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                    id_cliente integer not null,
                    foreign key (id_cliente) references clientes(id_cliente));

CREATE TABLE detalle_venta(id_venta integer not null
                            , id_producto integer not null
                            ,cantidad int not null check (cantidad >0)
                            ,precio_unitario numeric(10,2) not NULL check (precio_unitario >=0)
                            ,foreign key(id_venta) references ventas(id_venta)
                            ,foreign key(id_producto) references productos(id_producto)
                            ,primary key(id_venta, id_producto));

CREATE TABLE proveedores(id_proveedor  integer generated always as identity primary key
                        ,nombre_proveedor varchar(50) not null
                        ,correo_proveedor varchar(50) unique
                        ,telefono_proveedor varchar(15) unique
                        ,check (correo_proveedor is not null or telefono_proveedor is not null));

CREATE TABLE compras(id_compra integer generated always as identity primary KEY
                    ,fecha_compra TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
                    ,id_proveedor integer not null
                    ,foreign key (id_proveedor) references proveedores(id_proveedor));      

CREATE TABLE detalle_compra(id_compra integer not null
                            , id_producto integer not null
                            ,cantidad int not null check (cantidad >0)
                            ,costo_unitario numeric(10,2) not NULL check (costo_unitario >=0)
                            ,foreign key(id_compra) references compras(id_compra)
                            ,foreign key(id_producto) references productos(id_producto)
                            ,primary key(id_compra, id_producto));