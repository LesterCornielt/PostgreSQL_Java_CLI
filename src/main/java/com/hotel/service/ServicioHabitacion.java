package com.hotel.service;

import com.hotel.model.Habitacion;
import com.hotel.model.CaracteristicaHabitacion;
import com.hotel.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServicioHabitacion {
    
    public static List<Habitacion> obtenerHabitacionesDisponibles() throws SQLException {
        List<Habitacion> habitaciones = new ArrayList<>();
        String sql = "SELECT * FROM habitacion WHERE estado = 'DISPONIBLE' ORDER BY num_habitacion";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Habitacion habitacion = new Habitacion();
                habitacion.setNumHabitacion(rs.getString("num_habitacion"));
                habitacion.setPiso(rs.getInt("piso"));
                habitacion.setEstado(rs.getString("estado"));
                habitacion.setDisponible(rs.getBoolean("disponible"));
                habitacion.setIdTipo(rs.getInt("id_tipo"));
                habitaciones.add(habitacion);
            }
        }
        return habitaciones;
    }

    public static Habitacion obtenerHabitacionPorId(String numHabitacion) throws SQLException {
        String sql = "SELECT * FROM habitacion WHERE num_habitacion = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, numHabitacion);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Habitacion habitacion = new Habitacion();
                    habitacion.setNumHabitacion(rs.getString("num_habitacion"));
                    habitacion.setPiso(rs.getInt("piso"));
                    habitacion.setEstado(rs.getString("estado"));
                    habitacion.setDisponible(rs.getBoolean("disponible"));
                    habitacion.setIdTipo(rs.getInt("id_tipo"));
                    return habitacion;
                }
            }
        }
        return null;
    }

    public static List<CaracteristicaHabitacion> obtenerCaracteristicasHabitacion(String numHabitacion) throws SQLException {
        List<CaracteristicaHabitacion> caracteristicas = new ArrayList<>();
        String sql = "SELECT * FROM servicios_tipo WHERE id_tipo = (SELECT id_tipo FROM habitacion WHERE num_habitacion = ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, numHabitacion);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CaracteristicaHabitacion caracteristica = new CaracteristicaHabitacion();
                    caracteristica.setIdCaracteristica(rs.getInt("id_tipo"));
                    caracteristica.setNumHabitacion(numHabitacion);
                    caracteristica.setNombreCaracteristica("servicio");
                    caracteristica.setValorCaracteristica(rs.getString("servicio"));
                    caracteristicas.add(caracteristica);
                }
            }
        }
        return caracteristicas;
    }

    public static boolean agregarHabitacion(Habitacion habitacion) throws SQLException {
        String sql = "INSERT INTO habitacion (num_habitacion, piso, estado, disponible, id_tipo) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, habitacion.getNumHabitacion());
            stmt.setInt(2, habitacion.getPiso());
            stmt.setString(3, habitacion.getEstado());
            stmt.setBoolean(4, habitacion.getDisponible());
            stmt.setInt(5, habitacion.getIdTipo());
            
            return stmt.executeUpdate() > 0;
        }
    }

    public static boolean actualizarHabitacion(Habitacion habitacion) throws SQLException {
        String sql = "UPDATE habitacion SET piso = ?, estado = ?, disponible = ?, id_tipo = ? " +
                    "WHERE num_habitacion = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, habitacion.getPiso());
            stmt.setString(2, habitacion.getEstado());
            stmt.setBoolean(3, habitacion.getDisponible());
            stmt.setInt(4, habitacion.getIdTipo());
            stmt.setString(5, habitacion.getNumHabitacion());
            
            return stmt.executeUpdate() > 0;
        }
    }

    public static boolean eliminarHabitacion(String numHabitacion) throws SQLException {
        String sql = "DELETE FROM habitacion WHERE num_habitacion = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, numHabitacion);
            return stmt.executeUpdate() > 0;
        }
    }

    public static boolean estaHabitacionDisponible(String numHabitacion, java.time.LocalDate fechaEntrada, 
                                        java.time.LocalDate fechaSalida) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reserva r " +
                    "JOIN reserva_habitacion rh ON r.id_reserva = rh.id_reserva " +
                    "WHERE rh.num_habitacion = ? AND r.estado != 'CANCELADA' " +
                    "AND ((r.fecha_inicio <= ? AND r.fecha_fin >= ?) " +
                    "OR (r.fecha_inicio <= ? AND r.fecha_fin >= ?) " +
                    "OR (r.fecha_inicio >= ? AND r.fecha_fin <= ?))";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, numHabitacion);
            stmt.setDate(2, java.sql.Date.valueOf(fechaSalida));
            stmt.setDate(3, java.sql.Date.valueOf(fechaEntrada));
            stmt.setDate(4, java.sql.Date.valueOf(fechaSalida));
            stmt.setDate(5, java.sql.Date.valueOf(fechaEntrada));
            stmt.setDate(6, java.sql.Date.valueOf(fechaEntrada));
            stmt.setDate(7, java.sql.Date.valueOf(fechaSalida));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        }
        return false;
    }
} 