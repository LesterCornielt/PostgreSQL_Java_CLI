package com.hotel.service;

import com.hotel.model.Room;
import com.hotel.model.RoomFeature;
import com.hotel.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomService {
    
    public static List<Room> getAvailableRooms() throws SQLException {
        List<Room> habitaciones = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE status = 'DISPONIBLE' ORDER BY room_number";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Room habitacion = new Room();
                habitacion.setRoomId(rs.getInt("room_id"));
                habitacion.setRoomNumber(rs.getString("room_number"));
                habitacion.setRoomType(rs.getString("room_type"));
                habitacion.setCapacity(rs.getInt("capacity"));
                habitacion.setPricePerNight(rs.getBigDecimal("price_per_night"));
                habitacion.setDescription(rs.getString("description"));
                habitacion.setStatus(rs.getString("status"));
                habitacion.setFeatures(getRoomFeatures(habitacion.getRoomId()));
                habitaciones.add(habitacion);
            }
        }
        return habitaciones;
    }

    public static Room getRoomById(int idHabitacion) throws SQLException {
        String sql = "SELECT * FROM rooms WHERE room_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idHabitacion);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Room habitacion = new Room();
                    habitacion.setRoomId(rs.getInt("room_id"));
                    habitacion.setRoomNumber(rs.getString("room_number"));
                    habitacion.setRoomType(rs.getString("room_type"));
                    habitacion.setCapacity(rs.getInt("capacity"));
                    habitacion.setPricePerNight(rs.getBigDecimal("price_per_night"));
                    habitacion.setDescription(rs.getString("description"));
                    habitacion.setStatus(rs.getString("status"));
                    habitacion.setFeatures(getRoomFeatures(habitacion.getRoomId()));
                    return habitacion;
                }
            }
        }
        return null;
    }

    public static List<RoomFeature> getRoomFeatures(int idHabitacion) throws SQLException {
        List<RoomFeature> caracteristicas = new ArrayList<>();
        String sql = "SELECT * FROM room_features WHERE room_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idHabitacion);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RoomFeature caracteristica = new RoomFeature();
                    caracteristica.setFeatureId(rs.getInt("feature_id"));
                    caracteristica.setRoomId(rs.getInt("room_id"));
                    caracteristica.setFeatureName(rs.getString("feature_name"));
                    caracteristica.setFeatureValue(rs.getString("feature_value"));
                    caracteristicas.add(caracteristica);
                }
            }
        }
        return caracteristicas;
    }

    public static boolean addRoom(Room habitacion) throws SQLException {
        String sql = "INSERT INTO rooms (room_number, room_type, capacity, price_per_night, description, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?) RETURNING room_id";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, habitacion.getRoomNumber());
            stmt.setString(2, habitacion.getRoomType());
            stmt.setInt(3, habitacion.getCapacity());
            stmt.setBigDecimal(4, habitacion.getPricePerNight());
            stmt.setString(5, habitacion.getDescription());
            stmt.setString(6, habitacion.getStatus());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int idHabitacion = rs.getInt("room_id");
                    if (habitacion.getFeatures() != null) {
                        for (RoomFeature caracteristica : habitacion.getFeatures()) {
                            addRoomFeature(idHabitacion, caracteristica);
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean addRoomFeature(int idHabitacion, RoomFeature caracteristica) throws SQLException {
        String sql = "INSERT INTO room_features (room_id, feature_name, feature_value) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idHabitacion);
            stmt.setString(2, caracteristica.getFeatureName());
            stmt.setString(3, caracteristica.getFeatureValue());
            
            return stmt.executeUpdate() > 0;
        }
    }

    public static boolean updateRoom(Room habitacion) throws SQLException {
        String sql = "UPDATE rooms SET room_number = ?, room_type = ?, capacity = ?, " +
                    "price_per_night = ?, description = ?, status = ? WHERE room_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, habitacion.getRoomNumber());
            stmt.setString(2, habitacion.getRoomType());
            stmt.setInt(3, habitacion.getCapacity());
            stmt.setBigDecimal(4, habitacion.getPricePerNight());
            stmt.setString(5, habitacion.getDescription());
            stmt.setString(6, habitacion.getStatus());
            stmt.setInt(7, habitacion.getRoomId());
            
            return stmt.executeUpdate() > 0;
        }
    }

    public static boolean deleteRoom(int idHabitacion) throws SQLException {
        String sql = "DELETE FROM rooms WHERE room_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idHabitacion);
            return stmt.executeUpdate() > 0;
        }
    }

    public static boolean isRoomAvailable(int idHabitacion, java.time.LocalDate fechaEntrada, 
                                        java.time.LocalDate fechaSalida) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reservations r " +
                    "JOIN reservation_details rd ON r.reservation_id = rd.reservation_id " +
                    "WHERE rd.room_id = ? AND r.status != 'CANCELADA' " +
                    "AND ((r.check_in_date <= ? AND r.check_out_date >= ?) " +
                    "OR (r.check_in_date <= ? AND r.check_out_date >= ?) " +
                    "OR (r.check_in_date >= ? AND r.check_out_date <= ?))";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idHabitacion);
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