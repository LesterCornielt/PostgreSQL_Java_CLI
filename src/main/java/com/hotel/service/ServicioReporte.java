package com.hotel.service;

import com.hotel.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServicioReporte {
    
    public static Map<String, Object> obtenerEstadisticasOcupacion() throws SQLException {
        Map<String, Object> estadisticas = new HashMap<>();
        List<Map<String, Object>> estadisticasHabitaciones = new ArrayList<>();
        
        String sql = "SELECT h.num_habitacion, h.piso, t.nombre as tipo_habitacion, " +
                    "COUNT(DISTINCT r.id_reserva) as total_reservas, " +
                    "SUM(DATE_PART('day', r.fecha_fin - r.fecha_inicio)) as total_dias_ocupada " +
                    "FROM habitacion h " +
                    "LEFT JOIN reserva_habitacion rh ON h.num_habitacion = rh.num_habitacion " +
                    "LEFT JOIN reserva r ON rh.id_reserva = r.id_reserva " +
                    "LEFT JOIN tipo_habitacion t ON h.id_tipo = t.id_tipo " +
                    "WHERE r.estado != 'CANCELADA' OR r.estado IS NULL " +
                    "GROUP BY h.num_habitacion, h.piso, t.nombre";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> habitacion = new HashMap<>();
                habitacion.put("numHabitacion", rs.getString("num_habitacion"));
                habitacion.put("piso", rs.getInt("piso"));
                habitacion.put("tipoHabitacion", rs.getString("tipo_habitacion"));
                habitacion.put("totalReservas", rs.getInt("total_reservas"));
                habitacion.put("totalDiasOcupada", rs.getInt("total_dias_ocupada"));
                estadisticasHabitaciones.add(habitacion);
            }
        }
        
        estadisticas.put("estadisticasHabitaciones", estadisticasHabitaciones);
        return estadisticas;
    }

    public static Map<String, Object> obtenerIngresosPorMes() throws SQLException {
        Map<String, Object> ingresos = new HashMap<>();
        List<Map<String, Object>> ingresosMensuales = new ArrayList<>();
        
        String sql = "SELECT EXTRACT(YEAR FROM fecha_inicio) as año, " +
                    "EXTRACT(MONTH FROM fecha_inicio) as mes, " +
                    "SUM(total_estadia) as total_ingresos " +
                    "FROM reserva " +
                    "WHERE estado != 'CANCELADA' " +
                    "GROUP BY EXTRACT(YEAR FROM fecha_inicio), EXTRACT(MONTH FROM fecha_inicio) " +
                    "ORDER BY año, mes";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> mes = new HashMap<>();
                mes.put("año", rs.getInt("año"));
                mes.put("mes", rs.getInt("mes"));
                mes.put("totalIngresos", rs.getBigDecimal("total_ingresos"));
                ingresosMensuales.add(mes);
            }
        }
        
        ingresos.put("ingresosMensuales", ingresosMensuales);
        return ingresos;
    }

    public static Map<String, Object> obtenerReservasClientes() throws SQLException {
        Map<String, Object> reservas = new HashMap<>();
        List<Map<String, Object>> reservasClientes = new ArrayList<>();
        
        String sql = "SELECT c.id_cliente, c.nombre, c.apellido, " +
                    "r.id_reserva, r.fecha_inicio, r.fecha_fin, " +
                    "r.estado, COUNT(rh.num_habitacion) as num_habitaciones, r.total_estadia " +
                    "FROM cliente c " +
                    "JOIN reserva r ON c.id_cliente = r.id_cliente " +
                    "LEFT JOIN reserva_habitacion rh ON r.id_reserva = rh.id_reserva " +
                    "GROUP BY c.id_cliente, c.nombre, c.apellido, " +
                    "r.id_reserva, r.fecha_inicio, r.fecha_fin, r.estado, r.total_estadia " +
                    "ORDER BY r.fecha_inicio DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> reserva = new HashMap<>();
                reserva.put("idCliente", rs.getInt("id_cliente"));
                reserva.put("nombre", rs.getString("nombre"));
                reserva.put("apellido", rs.getString("apellido"));
                reserva.put("idReserva", rs.getInt("id_reserva"));
                reserva.put("fechaInicio", rs.getDate("fecha_inicio"));
                reserva.put("fechaFin", rs.getDate("fecha_fin"));
                reserva.put("estado", rs.getString("estado"));
                reserva.put("numHabitaciones", rs.getInt("num_habitaciones"));
                reserva.put("totalEstadia", rs.getBigDecimal("total_estadia"));
                reservasClientes.add(reserva);
            }
        }
        
        reservas.put("reservasClientes", reservasClientes);
        return reservas;
    }

    public static Map<String, Object> obtenerReporteHabitacionesDisponibles() throws SQLException {
        Map<String, Object> habitaciones = new HashMap<>();
        List<Map<String, Object>> habitacionesDisponibles = new ArrayList<>();
        
        String sql = "SELECT h.num_habitacion, h.piso, t.nombre as tipo_habitacion, t.capacidad, " +
                    "t.precio_por_noche, h.estado, " +
                    "COUNT(DISTINCT r.id_reserva) as reservas_actuales " +
                    "FROM habitacion h " +
                    "LEFT JOIN tipo_habitacion t ON h.id_tipo = t.id_tipo " +
                    "LEFT JOIN reserva_habitacion rh ON h.num_habitacion = rh.num_habitacion " +
                    "LEFT JOIN reserva r ON rh.id_reserva = r.id_reserva " +
                    "AND r.estado != 'CANCELADA' " +
                    "AND CURRENT_DATE BETWEEN r.fecha_inicio AND r.fecha_fin " +
                    "GROUP BY h.num_habitacion, h.piso, t.nombre, t.capacidad, " +
                    "t.precio_por_noche, h.estado " +
                    "ORDER BY h.num_habitacion";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> habitacion = new HashMap<>();
                habitacion.put("numHabitacion", rs.getString("num_habitacion"));
                habitacion.put("piso", rs.getInt("piso"));
                habitacion.put("tipoHabitacion", rs.getString("tipo_habitacion"));
                habitacion.put("capacidad", rs.getInt("capacidad"));
                habitacion.put("precioPorNoche", rs.getBigDecimal("precio_por_noche"));
                habitacion.put("estado", rs.getString("estado"));
                habitacion.put("reservasActuales", rs.getInt("reservas_actuales"));
                habitacionesDisponibles.add(habitacion);
            }
        }
        
        habitaciones.put("habitacionesDisponibles", habitacionesDisponibles);
        return habitaciones;
    }
} 