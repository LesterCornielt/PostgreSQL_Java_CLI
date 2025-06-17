-- Funciones SQL implementadas
-- 1. fn_ingresos_por_tipo
-- Descripción: Calcula los ingresos generados por cada tipo de habitación, sumando los montos de los pagos asociados.
CREATE OR REPLACE FUNCTION fn_ingresos_por_tipo()
RETURNS TABLE (
    tipo_habitacion VARCHAR,
    ingreso_total DECIMAL(10,2)
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        th.nombre AS tipo_habitacion,
        COALESCE(SUM(p.monto), 0) AS ingreso_total
    FROM tipo_habitacion th
    LEFT JOIN habitacion h ON h.id_tipo = th.id_tipo
    LEFT JOIN reserva_habitacion rh ON h.num_habitacion = rh.num_habitacion
    LEFT JOIN reserva r ON rh.id_reserva = r.id_reserva
    LEFT JOIN factura f ON r.id_reserva = f.id_reserva
    LEFT JOIN pago p ON f.id_factura = p.id_factura
    GROUP BY th.nombre
    ORDER BY ingreso_total DESC;
END;

-- 2. fn_obtener_huesped_frecuente
-- Descripción: Identifica al cliente con más reservas, mostrando su ID, nombre completo y número de reservas.
CREATE OR REPLACE FUNCTION fn_obtener_huesped_frecuente()
RETURNS TABLE (
    id_cliente INTEGER,
    nombre_completo VARCHAR,
    total_reservas BIGINT
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        c.id_cliente,
        CONCAT(c.primer_nombre, ' ', COALESCE(c.segundo_nombre, '')) AS nombre_completo,
        COUNT(r.id_reserva) AS total_reservas
    FROM cliente c
    LEFT JOIN reserva r ON c.id_cliente = r.id_cliente
    GROUP BY c.id_cliente, c.primer_nombre, c.segundo_nombre
    ORDER BY total_reservas DESC
    LIMIT 1;
END;
-- 3. fn_calcular_ocupacion
-- Descripción: Calcula el porcentaje de ocupación diaria para una fecha dada, basado en habitaciones reservadas u ocupadas.
CREATE OR REPLACE FUNCTION fn_calcular_ocupacion(fecha DATE)
RETURNS TABLE (
    fecha DATE,
    porcentaje_ocupacion DECIMAL(5,2)
) AS $$
DECLARE
    total_habitaciones INTEGER;
BEGIN
    SELECT COUNT(*) INTO total_habitaciones FROM habitacion WHERE estado != 'MANTENIMIENTO';
    
    RETURN QUERY
    SELECT 
        fecha AS fecha,
        CASE 
            WHEN total_habitaciones = 0 THEN 0
            ELSE (COUNT(rh.num_habitacion)::DECIMAL / total_habitaciones * 100)::DECIMAL(5,2)
        END AS porcentaje_ocupacion
    FROM reserva_habitacion rh
    WHERE rh.fecha_especifica = fecha
        AND rh.estado IN ('RESERVADA', 'OCUPADA')
    GROUP BY fecha;
END;
-- 4. fn_servicios_mas_solicitados
-- Descripción: Muestra los servicios adicionales más utilizados, su frecuencia y el costo total acumulado.
CREATE OR REPLACE FUNCTION fn_servicios_mas_solicitados()
RETURNS TABLE (
    nombre_servicio VARCHAR,
    veces_solicitado BIGINT,
    costo_total DECIMAL(10,2)
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        s.nombre AS nombre_servicio,
        COUNT(rs.id_servicio) AS veces_solicitado,
        COALESCE(SUM(s.costo), 0) AS costo_total
    FROM servicio_adicional s
    LEFT JOIN reserva_servicio rs ON s.id_servicio = rs.id_servicio
    GROUP BY s.nombre
    ORDER BY veces_solicitado DESC;
END;

