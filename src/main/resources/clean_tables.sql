-- Desactivar temporalmente las restricciones de clave foránea
SET CONSTRAINTS ALL DEFERRED;

-- Limpiar las tablas en orden inverso a sus dependencias
TRUNCATE TABLE pago CASCADE;
TRUNCATE TABLE factura CASCADE;
TRUNCATE TABLE reserva_servicio CASCADE;
TRUNCATE TABLE servicio_adicional CASCADE;
TRUNCATE TABLE reserva_habitacion CASCADE;
TRUNCATE TABLE reserva CASCADE;
TRUNCATE TABLE email_cliente CASCADE;
TRUNCATE TABLE telefono_cliente CASCADE;
TRUNCATE TABLE cliente CASCADE;
TRUNCATE TABLE gerente CASCADE;
TRUNCATE TABLE empleado CASCADE;
TRUNCATE TABLE habitacion CASCADE;
TRUNCATE TABLE servicios_tipo CASCADE;
TRUNCATE TABLE tipo_habitacion CASCADE;

-- Reiniciar las secuencias
ALTER SEQUENCE cliente_id_cliente_seq RESTART WITH 1;
ALTER SEQUENCE empleado_id_empleado_seq RESTART WITH 1;
ALTER SEQUENCE tipo_habitacion_id_tipo_seq RESTART WITH 1;
ALTER SEQUENCE servicio_adicional_id_servicio_seq RESTART WITH 1;
ALTER SEQUENCE reserva_id_reserva_seq RESTART WITH 1;
ALTER SEQUENCE factura_id_factura_seq RESTART WITH 1;
ALTER SEQUENCE pago_id_pago_seq RESTART WITH 1;

-- Reactivar las restricciones de clave foránea
SET CONSTRAINTS ALL IMMEDIATE; 