-- Creación de la base de datos
CREATE DATABASE hotel_reservation;

-- Conectar a la base de datos
\c hotel_reservation;

-- Tabla de Clientes
CREATE TABLE cliente (
    id_cliente SERIAL PRIMARY KEY,
    primer_nombre VARCHAR(50) NOT NULL,
    segundo_nombre VARCHAR(50),
    sexo CHAR(1) CHECK (sexo IN ('M', 'F', 'O')),
    fecha_nacimiento DATE NOT NULL,
    tipo_cliente VARCHAR(20) NOT NULL
);

-- Tabla de Teléfonos de Cliente
CREATE TABLE telefono_cliente (
    id_cliente INTEGER REFERENCES cliente(id_cliente),
    telefono VARCHAR(15) NOT NULL,
    PRIMARY KEY (id_cliente, telefono)
);

-- Tabla de Emails de Cliente
CREATE TABLE email_cliente (
    id_cliente INTEGER REFERENCES cliente(id_cliente),
    email VARCHAR(100) NOT NULL,
    PRIMARY KEY (id_cliente, email)
);

-- Tabla de Tipos de Habitación
CREATE TABLE tipo_habitacion (
    id_tipo SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion TEXT
);

-- Tabla de Servicios por Tipo de Habitación
CREATE TABLE servicios_tipo (
    id_tipo INTEGER REFERENCES tipo_habitacion(id_tipo),
    servicio VARCHAR(100) NOT NULL,
    PRIMARY KEY (id_tipo, servicio)
);

-- Tabla de Habitaciones
CREATE TABLE habitacion (
    num_habitacion VARCHAR(10) PRIMARY KEY,
    piso INTEGER NOT NULL,
    estado VARCHAR(20) DEFAULT 'DISPONIBLE' CHECK (estado IN ('DISPONIBLE', 'OCUPADA', 'MANTENIMIENTO')),
    disponible BOOLEAN DEFAULT true,
    id_tipo INTEGER REFERENCES tipo_habitacion(id_tipo)
);

-- Tabla de Empleados
CREATE TABLE empleado (
    id_empleado SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    cargo VARCHAR(50) NOT NULL,
    supervisor_id INTEGER REFERENCES empleado(id_empleado)
);

-- Tabla de Gerentes
CREATE TABLE gerente (
    id_gerente INTEGER PRIMARY KEY REFERENCES empleado(id_empleado),
    area_responsable VARCHAR(50) NOT NULL
);

-- Tabla de Reservas
CREATE TABLE reserva (
    id_reserva SERIAL PRIMARY KEY,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    total_estadia DECIMAL(10,2) NOT NULL,
    id_cliente INTEGER REFERENCES cliente(id_cliente),
    id_empleado INTEGER REFERENCES empleado(id_empleado),
    CONSTRAINT valid_dates CHECK (fecha_inicio < fecha_fin)
);

-- Tabla de Detalles de Reserva por Habitación
CREATE TABLE reserva_habitacion (
    id_reserva INTEGER REFERENCES reserva(id_reserva),
    num_habitacion VARCHAR(10) REFERENCES habitacion(num_habitacion),
    fecha_especifica DATE NOT NULL,
    estado VARCHAR(20) DEFAULT 'RESERVADA' CHECK (estado IN ('RESERVADA', 'OCUPADA', 'LIBERADA')),
    PRIMARY KEY (id_reserva, num_habitacion, fecha_especifica)
);

-- Tabla de Facturas
CREATE TABLE factura (
    id_factura SERIAL PRIMARY KEY,
    fecha_emision TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    monto_total DECIMAL(10,2) NOT NULL,
    id_reserva INTEGER REFERENCES reserva(id_reserva)
);

-- Tabla de Pagos
CREATE TABLE pago (
    id_pago SERIAL PRIMARY KEY,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    monto DECIMAL(10,2) NOT NULL,
    metodo VARCHAR(50) NOT NULL,
    id_factura INTEGER REFERENCES factura(id_factura)
);

-- Tabla de Servicios Adicionales
CREATE TABLE servicio_adicional (
    id_servicio SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    costo DECIMAL(10,2) NOT NULL
);

-- Tabla de Servicios por Reserva
CREATE TABLE reserva_servicio (
    id_reserva INTEGER REFERENCES reserva(id_reserva),
    id_servicio INTEGER REFERENCES servicio_adicional(id_servicio),
    PRIMARY KEY (id_reserva, id_servicio)
);

-- Vista que muestra las habitaciones disponibles y su número actual de reservas
CREATE VIEW habitaciones_disponibles AS
SELECT h.*, COUNT(rh.num_habitacion) as reservas_actuales
FROM habitacion h
LEFT JOIN reserva_habitacion rh ON h.num_habitacion = rh.num_habitacion
LEFT JOIN reserva r ON rh.id_reserva = r.id_reserva
WHERE h.estado = 'DISPONIBLE'
GROUP BY h.num_habitacion;

-- Vista que muestra las reservas de cada cliente con detalles
CREATE VIEW reservas_cliente AS
SELECT 
    c.id_cliente,
    c.primer_nombre,
    c.segundo_nombre,
    r.id_reserva,
    r.fecha_inicio,
    r.fecha_fin,
    rh.estado as estado_reserva,
    COUNT(rh.num_habitacion) as numero_habitaciones,
    r.total_estadia
FROM cliente c
JOIN reserva r ON c.id_cliente = r.id_cliente
JOIN reserva_habitacion rh ON r.id_reserva = rh.id_reserva
GROUP BY c.id_cliente, r.id_reserva, rh.estado;

-- Vista que muestra estadísticas de ocupación por habitación
CREATE VIEW estadisticas_ocupacion_habitacion AS
SELECT 
    h.num_habitacion,
    h.piso,
    th.nombre as tipo_habitacion,
    COUNT(r.id_reserva) as total_reservas,
    COALESCE(SUM(DATE_PART('day', r.fecha_fin::timestamp - r.fecha_inicio::timestamp)), 0) as total_dias_ocupados
FROM habitacion h
LEFT JOIN tipo_habitacion th ON h.id_tipo = th.id_tipo
LEFT JOIN reserva_habitacion rh ON h.num_habitacion = rh.num_habitacion
LEFT JOIN reserva r ON rh.id_reserva = r.id_reserva
GROUP BY h.num_habitacion, h.piso, th.nombre;

-- Vista que muestra los ingresos mensuales
CREATE VIEW ingresos_por_mes AS
SELECT 
    EXTRACT(YEAR FROM p.fecha) as año,
    EXTRACT(MONTH FROM p.fecha) as mes,
    SUM(p.monto) as ingreso_total
FROM pago p
JOIN factura f ON p.id_factura = f.id_factura
WHERE f.fecha_emision IS NOT NULL
GROUP BY EXTRACT(YEAR FROM p.fecha), EXTRACT(MONTH FROM p.fecha)
ORDER BY año, mes; 