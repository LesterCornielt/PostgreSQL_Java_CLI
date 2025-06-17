-- Insertar tipos de habitación
INSERT INTO tipo_habitacion (id_tipo, nombre, descripcion) VALUES
(1, 'Suite Presidencial', 'Habitación de lujo con sala de estar y jacuzzi'),
(2, 'Suite Ejecutiva', 'Habitación amplia con sala de estar'),
(3, 'Habitación Doble', 'Habitación con dos camas queen'),
(4, 'Habitación Individual', 'Habitación con una cama king');

-- Insertar servicios por tipo de habitación
INSERT INTO servicios_tipo (id_tipo, servicio) VALUES
(1, 'Desayuno buffet'),
(1, 'Servicio de conserjería 24/7'),
(1, 'Acceso a spa'),
(2, 'Desayuno continental'),
(2, 'Servicio de limpieza diario'),
(3, 'Desayuno continental'),
(4, 'Desayuno continental');

-- Insertar habitaciones
INSERT INTO habitacion (num_habitacion, piso, estado, disponible, id_tipo) VALUES
('101', 1, 'DISPONIBLE', true, 4),
('102', 1, 'DISPONIBLE', true, 4),
('201', 2, 'DISPONIBLE', true, 3),
('202', 2, 'DISPONIBLE', true, 3),
('301', 3, 'DISPONIBLE', true, 2),
('401', 4, 'DISPONIBLE', true, 1);

-- Insertar empleados
INSERT INTO empleado (id_empleado, nombre, cargo, supervisor_id) VALUES
(1, 'Juan Pérez', 'Gerente General', NULL),
(2, 'María García', 'Gerente de Recepción', 1),
(3, 'Carlos López', 'Recepcionista', 2),
(4, 'Ana Martínez', 'Limpieza', 2);

-- Insertar gerentes
INSERT INTO gerente (id_gerente, area_responsable) VALUES
(1, 'Administración General'),
(2, 'Recepción y Atención al Cliente');

-- Insertar clientes
INSERT INTO cliente (id_cliente, primer_nombre, segundo_nombre, sexo, fecha_nacimiento, tipo_cliente) VALUES
(1, 'Roberto', 'Sánchez', 'M', '1985-05-15', 'REGULAR'),
(2, 'Laura', 'González', 'F', '1990-08-22', 'VIP'),
(3, 'Miguel', 'Rodríguez', 'M', '1978-11-30', 'REGULAR'),
(4, 'Patricia', 'Martínez', 'F', '1995-03-10', 'VIP');

-- Insertar teléfonos de clientes
INSERT INTO telefono_cliente (id_cliente, telefono) VALUES
(1, '555-0101'),
(1, '555-0102'),
(2, '555-0201'),
(3, '555-0301'),
(4, '555-0401');

-- Insertar emails de clientes
INSERT INTO email_cliente (id_cliente, email) VALUES
(1, 'roberto.sanchez@email.com'),
(2, 'laura.gonzalez@email.com'),
(3, 'miguel.rodriguez@email.com'),
(4, 'patricia.martinez@email.com');

-- Insertar servicios adicionales
INSERT INTO servicio_adicional (id_servicio, nombre, costo) VALUES
(1, 'Servicio de Spa', 150.00),
(2, 'Cena Gourmet', 200.00),
(3, 'Servicio de Lavandería', 50.00),
(4, 'Tour Guiado', 75.00);

-- Insertar reservas
INSERT INTO reserva (id_reserva, fecha_inicio, fecha_fin, total_estadia, id_cliente, id_empleado) VALUES
(1, '2024-03-01', '2024-03-05', 1200.00, 1, 3),
(2, '2024-03-10', '2024-03-15', 2500.00, 2, 3),
(3, '2024-03-20', '2024-03-25', 1800.00, 3, 3),
(4, '2024-04-01', '2024-04-05', 3000.00, 4, 3);

-- Insertar detalles de reserva por habitación
INSERT INTO reserva_habitacion (id_reserva, num_habitacion, fecha_especifica, estado) VALUES
(1, '101', '2024-03-01', 'RESERVADA'),
(1, '101', '2024-03-02', 'RESERVADA'),
(1, '101', '2024-03-03', 'RESERVADA'),
(1, '101', '2024-03-04', 'RESERVADA'),
(2, '301', '2024-03-10', 'RESERVADA'),
(2, '301', '2024-03-11', 'RESERVADA'),
(2, '301', '2024-03-12', 'RESERVADA'),
(2, '301', '2024-03-13', 'RESERVADA'),
(2, '301', '2024-03-14', 'RESERVADA'),
(3, '201', '2024-03-20', 'RESERVADA'),
(3, '201', '2024-03-21', 'RESERVADA'),
(3, '201', '2024-03-22', 'RESERVADA'),
(3, '201', '2024-03-23', 'RESERVADA'),
(3, '201', '2024-03-24', 'RESERVADA'),
(4, '401', '2024-04-01', 'RESERVADA'),
(4, '401', '2024-04-02', 'RESERVADA'),
(4, '401', '2024-04-03', 'RESERVADA'),
(4, '401', '2024-04-04', 'RESERVADA');

-- Insertar facturas
INSERT INTO factura (id_factura, fecha_emision, monto_total, id_reserva) VALUES
(1, '2024-03-01 10:00:00', 1200.00, 1),
(2, '2024-03-10 11:00:00', 2500.00, 2),
(3, '2024-03-20 09:00:00', 1800.00, 3),
(4, '2024-04-01 14:00:00', 3000.00, 4);

-- Insertar pagos
INSERT INTO pago (id_pago, fecha, monto, metodo, id_factura) VALUES
(1, '2024-03-01 10:05:00', 1200.00, 'TARJETA_CREDITO', 1),
(2, '2024-03-10 11:05:00', 2500.00, 'TARJETA_CREDITO', 2),
(3, '2024-03-20 09:05:00', 1800.00, 'EFECTIVO', 3),
(4, '2024-04-01 14:05:00', 3000.00, 'TRANSFERENCIA', 4);

-- Insertar servicios por reserva
INSERT INTO reserva_servicio (id_reserva, id_servicio) VALUES
(1, 1), -- Spa para reserva 1
(1, 3), -- Lavandería para reserva 1
(2, 2), -- Cena Gourmet para reserva 2
(2, 4), -- Tour Guiado para reserva 2
(3, 1), -- Spa para reserva 3
(4, 1), -- Spa para reserva 4
(4, 2); -- Cena Gourmet para reserva 4 