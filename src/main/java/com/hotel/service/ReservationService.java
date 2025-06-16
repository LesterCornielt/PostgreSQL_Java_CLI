package com.hotel.service;

import com.hotel.model.Reservation;
import com.hotel.model.ReservationDetail;
import com.hotel.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {
    
    public static Reservation createReservation(Reservation reserva) throws SQLException {
        String sql = "INSERT INTO reservations (client_id, check_in_date, check_out_date, " +
                    "number_of_guests, total_price, status) VALUES (?, ?, ?, ?, ?, ?) RETURNING *";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, reserva.getClientId());
            stmt.setDate(2, java.sql.Date.valueOf(reserva.getCheckInDate()));
            stmt.setDate(3, java.sql.Date.valueOf(reserva.getCheckOutDate()));
            stmt.setInt(4, reserva.getNumberOfGuests());
            stmt.setBigDecimal(5, reserva.getTotalPrice());
            stmt.setString(6, reserva.getStatus());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    reserva.setReservationId(rs.getInt("reservation_id"));
                    // Agregar detalles de la reserva
                    for (ReservationDetail detalle : reserva.getDetails()) {
                        addReservationDetail(reserva.getReservationId(), detalle);
                    }
                    return reserva;
                }
            }
        }
        return null;
    }

    public static boolean addReservationDetail(int idReserva, ReservationDetail detalle) throws SQLException {
        String sql = "INSERT INTO reservation_details (reservation_id, room_id, price_per_night) " +
                    "VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idReserva);
            stmt.setInt(2, detalle.getRoomId());
            stmt.setBigDecimal(3, detalle.getPricePerNight());
            
            return stmt.executeUpdate() > 0;
        }
    }

    public static List<Reservation> getReservationsByClient(int idCliente) throws SQLException {
        List<Reservation> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE client_id = ? ORDER BY check_in_date DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reservation reserva = new Reservation();
                    reserva.setReservationId(rs.getInt("reservation_id"));
                    reserva.setClientId(rs.getInt("client_id"));
                    reserva.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
                    reserva.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
                    reserva.setNumberOfGuests(rs.getInt("number_of_guests"));
                    reserva.setTotalPrice(rs.getBigDecimal("total_price"));
                    reserva.setStatus(rs.getString("status"));
                    reserva.setDetails(getReservationDetails(reserva.getReservationId()));
                    reservas.add(reserva);
                }
            }
        }
        return reservas;
    }

    public static List<Reservation> getAllReservations() throws SQLException {
        List<Reservation> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reservations ORDER BY check_in_date DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Reservation reserva = new Reservation();
                reserva.setReservationId(rs.getInt("reservation_id"));
                reserva.setClientId(rs.getInt("client_id"));
                reserva.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
                reserva.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
                reserva.setNumberOfGuests(rs.getInt("number_of_guests"));
                reserva.setTotalPrice(rs.getBigDecimal("total_price"));
                reserva.setStatus(rs.getString("status"));
                reserva.setDetails(getReservationDetails(reserva.getReservationId()));
                reservas.add(reserva);
            }
        }
        return reservas;
    }

    public static List<ReservationDetail> getReservationDetails(int idReserva) throws SQLException {
        List<ReservationDetail> detalles = new ArrayList<>();
        String sql = "SELECT * FROM reservation_details WHERE reservation_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idReserva);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ReservationDetail detalle = new ReservationDetail();
                    detalle.setReservationId(rs.getInt("reservation_id"));
                    detalle.setRoomId(rs.getInt("room_id"));
                    detalle.setPricePerNight(rs.getBigDecimal("price_per_night"));
                    detalles.add(detalle);
                }
            }
        }
        return detalles;
    }

    public static boolean cancelReservation(int idReserva) throws SQLException {
        String sql = "UPDATE reservations SET status = 'CANCELADA' WHERE reservation_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idReserva);
            return stmt.executeUpdate() > 0;
        }
    }

    public static boolean updateReservation(Reservation reserva) throws SQLException {
        String sql = "UPDATE reservations SET check_in_date = ?, check_out_date = ?, " +
                    "number_of_guests = ?, total_price = ?, status = ? WHERE reservation_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(reserva.getCheckInDate()));
            stmt.setDate(2, java.sql.Date.valueOf(reserva.getCheckOutDate()));
            stmt.setInt(3, reserva.getNumberOfGuests());
            stmt.setBigDecimal(4, reserva.getTotalPrice());
            stmt.setString(5, reserva.getStatus());
            stmt.setInt(6, reserva.getReservationId());
            
            return stmt.executeUpdate() > 0;
        }
    }
} 