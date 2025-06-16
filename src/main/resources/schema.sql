-- Creación de la base de datos
CREATE DATABASE hotel_reservation;

-- Conectar a la base de datos
\c hotel_reservation;

-- Tabla de Usuarios (entidad base para clientes, empleados y gerentes)
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('CLIENT', 'EMPLOYEE', 'MANAGER')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de Clientes (especialización de usuarios)
CREATE TABLE clients (
    client_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(user_id),
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    identification VARCHAR(20) UNIQUE NOT NULL,
    country VARCHAR(50) NOT NULL,
    gender CHAR(1) CHECK (gender IN ('M', 'F', 'O')),
    birth_date DATE NOT NULL,
    age INTEGER GENERATED ALWAYS AS (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM birth_date)) STORED
);

-- Tabla de Empleados (especialización de usuarios)
CREATE TABLE employees (
    employee_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(user_id),
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    position VARCHAR(50) NOT NULL,
    hire_date DATE NOT NULL,
    salary DECIMAL(10,2) NOT NULL
);

-- Tabla de Habitaciones
CREATE TABLE rooms (
    room_id SERIAL PRIMARY KEY,
    room_number VARCHAR(10) UNIQUE NOT NULL,
    room_type VARCHAR(50) NOT NULL,
    capacity INTEGER NOT NULL,
    price_per_night DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'OCCUPIED', 'MAINTENANCE')),
    description TEXT
);

-- Tabla de Características de Habitaciones (atributo multivaluado)
CREATE TABLE room_features (
    feature_id SERIAL PRIMARY KEY,
    room_id INTEGER REFERENCES rooms(room_id),
    feature_name VARCHAR(50) NOT NULL,
    feature_value VARCHAR(100) NOT NULL
);

-- Tabla de Reservas
CREATE TABLE reservations (
    reservation_id SERIAL PRIMARY KEY,
    client_id INTEGER REFERENCES clients(client_id),
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    number_of_guests INTEGER NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_dates CHECK (check_in_date < check_out_date)
);

-- Tabla de Detalles de Reserva (interrelación N:M entre Reservas y Habitaciones)
CREATE TABLE reservation_details (
    reservation_id INTEGER REFERENCES reservations(reservation_id),
    room_id INTEGER REFERENCES rooms(room_id),
    price_per_night DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (reservation_id, room_id)
);

-- Tabla de Pagos
CREATE TABLE payments (
    payment_id SERIAL PRIMARY KEY,
    reservation_id INTEGER REFERENCES reservations(reservation_id),
    amount DECIMAL(10,2) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_method VARCHAR(50) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED'))
);

-- Vistas
CREATE VIEW available_rooms AS
SELECT r.*, COUNT(rd.room_id) as current_reservations
FROM rooms r
LEFT JOIN reservation_details rd ON r.room_id = rd.room_id
LEFT JOIN reservations res ON rd.reservation_id = res.reservation_id
WHERE r.status = 'AVAILABLE'
GROUP BY r.room_id;

CREATE VIEW client_reservations AS
SELECT 
    c.client_id,
    c.first_name,
    c.last_name,
    r.reservation_id,
    r.check_in_date,
    r.check_out_date,
    r.status as reservation_status,
    COUNT(rd.room_id) as number_of_rooms,
    r.total_price
FROM clients c
JOIN reservations r ON c.client_id = r.client_id
JOIN reservation_details rd ON r.reservation_id = rd.reservation_id
GROUP BY c.client_id, r.reservation_id;

CREATE VIEW room_occupancy_stats AS
SELECT 
    r.room_id,
    r.room_number,
    r.room_type,
    COUNT(res.reservation_id) as total_reservations,
    SUM(EXTRACT(DAY FROM (res.check_out_date - res.check_in_date))) as total_days_occupied
FROM rooms r
LEFT JOIN reservation_details rd ON r.room_id = rd.room_id
LEFT JOIN reservations res ON rd.reservation_id = res.reservation_id
GROUP BY r.room_id;

CREATE VIEW revenue_by_month AS
SELECT 
    EXTRACT(YEAR FROM payment_date) as year,
    EXTRACT(MONTH FROM payment_date) as month,
    SUM(amount) as total_revenue
FROM payments
WHERE status = 'COMPLETED'
GROUP BY EXTRACT(YEAR FROM payment_date), EXTRACT(MONTH FROM payment_date)
ORDER BY year, month; 