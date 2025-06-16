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

public class ReportService {
    
    public static Map<String, Object> getRoomOccupancyStats() throws SQLException {
        Map<String, Object> estadisticas = new HashMap<>();
        List<Map<String, Object>> estadisticasHabitaciones = new ArrayList<>();
        
        String sql = "SELECT r.room_id, r.room_number, r.room_type, " +
                    "COUNT(DISTINCT res.reservation_id) as total_reservations, " +
                    "SUM(DATE_PART('day', res.check_out_date - res.check_in_date)) as total_days_occupied " +
                    "FROM rooms r " +
                    "LEFT JOIN reservation_details rd ON r.room_id = rd.room_id " +
                    "LEFT JOIN reservations res ON rd.reservation_id = res.reservation_id " +
                    "WHERE res.status != 'CANCELADA' OR res.status IS NULL " +
                    "GROUP BY r.room_id, r.room_number, r.room_type";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> habitacion = new HashMap<>();
                habitacion.put("roomId", rs.getInt("room_id"));
                habitacion.put("roomNumber", rs.getString("room_number"));
                habitacion.put("roomType", rs.getString("room_type"));
                habitacion.put("totalReservations", rs.getInt("total_reservations"));
                habitacion.put("totalDaysOccupied", rs.getInt("total_days_occupied"));
                estadisticasHabitaciones.add(habitacion);
            }
        }
        
        estadisticas.put("roomStats", estadisticasHabitaciones);
        return estadisticas;
    }

    public static Map<String, Object> getRevenueByMonth() throws SQLException {
        Map<String, Object> ingresos = new HashMap<>();
        List<Map<String, Object>> ingresosMensuales = new ArrayList<>();
        
        String sql = "SELECT EXTRACT(YEAR FROM check_in_date) as year, " +
                    "EXTRACT(MONTH FROM check_in_date) as month, " +
                    "SUM(total_price) as total_revenue " +
                    "FROM reservations " +
                    "WHERE status != 'CANCELADA' " +
                    "GROUP BY EXTRACT(YEAR FROM check_in_date), EXTRACT(MONTH FROM check_in_date) " +
                    "ORDER BY year, month";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> mes = new HashMap<>();
                mes.put("year", rs.getInt("year"));
                mes.put("month", rs.getInt("month"));
                mes.put("totalRevenue", rs.getBigDecimal("total_revenue"));
                ingresosMensuales.add(mes);
            }
        }
        
        ingresos.put("monthlyRevenue", ingresosMensuales);
        return ingresos;
    }

    public static Map<String, Object> getClientReservations() throws SQLException {
        Map<String, Object> reservas = new HashMap<>();
        List<Map<String, Object>> reservasClientes = new ArrayList<>();
        
        String sql = "SELECT c.client_id, c.first_name, c.last_name, " +
                    "r.reservation_id, r.check_in_date, r.check_out_date, " +
                    "r.status, COUNT(rd.room_id) as number_of_rooms, r.total_price " +
                    "FROM clients c " +
                    "JOIN reservations r ON c.client_id = r.client_id " +
                    "LEFT JOIN reservation_details rd ON r.reservation_id = rd.reservation_id " +
                    "GROUP BY c.client_id, c.first_name, c.last_name, " +
                    "r.reservation_id, r.check_in_date, r.check_out_date, r.status, r.total_price " +
                    "ORDER BY r.check_in_date DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> reserva = new HashMap<>();
                reserva.put("clientId", rs.getInt("client_id"));
                reserva.put("firstName", rs.getString("first_name"));
                reserva.put("lastName", rs.getString("last_name"));
                reserva.put("reservationId", rs.getInt("reservation_id"));
                reserva.put("checkInDate", rs.getDate("check_in_date"));
                reserva.put("checkOutDate", rs.getDate("check_out_date"));
                reserva.put("status", rs.getString("status"));
                reserva.put("numberOfRooms", rs.getInt("number_of_rooms"));
                reserva.put("totalPrice", rs.getBigDecimal("total_price"));
                reservasClientes.add(reserva);
            }
        }
        
        reservas.put("clientReservations", reservasClientes);
        return reservas;
    }

    public static Map<String, Object> getAvailableRoomsReport() throws SQLException {
        Map<String, Object> habitaciones = new HashMap<>();
        List<Map<String, Object>> habitacionesDisponibles = new ArrayList<>();
        
        String sql = "SELECT r.room_id, r.room_number, r.room_type, r.capacity, " +
                    "r.price_per_night, r.status, " +
                    "COUNT(DISTINCT res.reservation_id) as current_reservations " +
                    "FROM rooms r " +
                    "LEFT JOIN reservation_details rd ON r.room_id = rd.room_id " +
                    "LEFT JOIN reservations res ON rd.reservation_id = res.reservation_id " +
                    "AND res.status != 'CANCELADA' " +
                    "AND CURRENT_DATE BETWEEN res.check_in_date AND res.check_out_date " +
                    "GROUP BY r.room_id, r.room_number, r.room_type, r.capacity, " +
                    "r.price_per_night, r.status " +
                    "ORDER BY r.room_number";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> habitacion = new HashMap<>();
                habitacion.put("roomId", rs.getInt("room_id"));
                habitacion.put("roomNumber", rs.getString("room_number"));
                habitacion.put("roomType", rs.getString("room_type"));
                habitacion.put("capacity", rs.getInt("capacity"));
                habitacion.put("pricePerNight", rs.getBigDecimal("price_per_night"));
                habitacion.put("status", rs.getString("status"));
                habitacion.put("currentReservations", rs.getInt("current_reservations"));
                habitacionesDisponibles.add(habitacion);
            }
        }
        
        habitaciones.put("availableRooms", habitacionesDisponibles);
        return habitaciones;
    }
} 