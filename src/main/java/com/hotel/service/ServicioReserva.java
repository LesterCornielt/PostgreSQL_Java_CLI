package com.hotel.service;

import com.hotel.model.Reserva;
import com.hotel.model.DetalleReserva;
import com.hotel.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServicioReserva {
    
    public static Reserva crearReserva(Reserva reserva) throws SQLException {
        String sql = "INSERT INTO reserva (fecha_inicio, fecha_fin, total_estadia, " +
                    "id_cliente, id_empleado) VALUES (?, ?, ?, ?, ?) RETURNING *";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(reserva.getFechaInicio()));
            stmt.setDate(2, java.sql.Date.valueOf(reserva.getFechaFin()));
            stmt.setBigDecimal(3, reserva.getTotalEstadia());
            stmt.setInt(4, reserva.getIdCliente());
            stmt.setInt(5, reserva.getIdEmpleado());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    reserva.setIdReserva(rs.getInt("id_reserva"));
                    // Agregar detalles de la reserva
                    for (DetalleReserva detalle : reserva.getDetalles()) {
                        agregarDetalleReserva(reserva.getIdReserva(), detalle);
                    }
                    return reserva;
                }
            }
        }
        return null;
    }

    private static void agregarDetalleReserva(Integer idReserva, DetalleReserva detalle) throws SQLException {
        String sql = "INSERT INTO reserva_habitacion (id_reserva, num_habitacion, " +
                    "fecha_especifica, estado) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idReserva);
            stmt.setString(2, detalle.getNumHabitacion());
            stmt.setDate(3, java.sql.Date.valueOf(detalle.getFechaEspecifica()));
            stmt.setString(4, detalle.getEstado());
            
            stmt.executeUpdate();
        }
    }

    public static List<Reserva> obtenerReservasPorCliente(int idCliente) throws SQLException {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reserva WHERE id_cliente = ? ORDER BY fecha_inicio DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reserva reserva = new Reserva();
                    reserva.setIdReserva(rs.getInt("id_reserva"));
                    reserva.setIdCliente(rs.getInt("id_cliente"));
                    reserva.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
                    reserva.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
                    reserva.setTotalEstadia(rs.getBigDecimal("total_estadia"));
                    reserva.setIdEmpleado(rs.getInt("id_empleado"));
                    reserva.setDetalles(obtenerDetallesReserva(reserva.getIdReserva()));
                    reservas.add(reserva);
                }
            }
        }
        return reservas;
    }

    public static List<Reserva> obtenerTodasLasReservas() throws SQLException {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reserva ORDER BY fecha_inicio DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Reserva reserva = new Reserva();
                reserva.setIdReserva(rs.getInt("id_reserva"));
                reserva.setIdCliente(rs.getInt("id_cliente"));
                reserva.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
                reserva.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
                reserva.setTotalEstadia(rs.getBigDecimal("total_estadia"));
                reserva.setIdEmpleado(rs.getInt("id_empleado"));
                reserva.setDetalles(obtenerDetallesReserva(reserva.getIdReserva()));
                reservas.add(reserva);
            }
        }
        return reservas;
    }

    public static List<DetalleReserva> obtenerDetallesReserva(int idReserva) throws SQLException {
        List<DetalleReserva> detalles = new ArrayList<>();
        String sql = "SELECT * FROM reserva_habitacion WHERE id_reserva = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idReserva);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DetalleReserva detalle = new DetalleReserva();
                    detalle.setIdReserva(rs.getInt("id_reserva"));
                    detalle.setNumHabitacion(rs.getString("num_habitacion"));
                    detalle.setFechaEspecifica(rs.getDate("fecha_especifica").toLocalDate());
                    detalle.setEstado(rs.getString("estado"));
                    detalles.add(detalle);
                }
            }
        }
        return detalles;
    }

    public static boolean cancelarReserva(int idReserva) throws SQLException {
        String sql = "UPDATE reserva SET estado = 'CANCELADA' WHERE id_reserva = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idReserva);
            return stmt.executeUpdate() > 0;
        }
    }

    public static boolean actualizarReserva(Reserva reserva) throws SQLException {
        String sql = "UPDATE reserva SET fecha_inicio = ?, fecha_fin = ?, " +
                    "total_estadia = ?, id_cliente = ?, id_empleado = ? WHERE id_reserva = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(reserva.getFechaInicio()));
            stmt.setDate(2, java.sql.Date.valueOf(reserva.getFechaFin()));
            stmt.setBigDecimal(3, reserva.getTotalEstadia());
            stmt.setInt(4, reserva.getIdCliente());
            stmt.setInt(5, reserva.getIdEmpleado());
            stmt.setInt(6, reserva.getIdReserva());
            
            return stmt.executeUpdate() > 0;
        }
    }
} 