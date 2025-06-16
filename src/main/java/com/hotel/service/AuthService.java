package com.hotel.service;

import com.hotel.model.User;
import com.hotel.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuthService {
    private static User usuarioActual = null;

    public static User login(String usuario, String contraseña) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario);
            stmt.setString(2, contraseña);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    usuarioActual = new User();
                    usuarioActual.setUserId(rs.getInt("user_id"));
                    usuarioActual.setUsername(rs.getString("username"));
                    usuarioActual.setEmail(rs.getString("email"));
                    usuarioActual.setRole(rs.getString("role"));
                    return usuarioActual;
                }
            }
        }
        return null;
    }

    public static User register(String usuario, String contraseña, String correo, String rol) throws SQLException {
        String sql = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ?) RETURNING *";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario);
            stmt.setString(2, contraseña);
            stmt.setString(3, correo);
            stmt.setString(4, rol);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User nuevoUsuario = new User();
                    nuevoUsuario.setUserId(rs.getInt("user_id"));
                    nuevoUsuario.setUsername(rs.getString("username"));
                    nuevoUsuario.setEmail(rs.getString("email"));
                    nuevoUsuario.setRole(rs.getString("role"));
                    return nuevoUsuario;
                }
            }
        }
        return null;
    }

    public static void logout() {
        usuarioActual = null;
    }

    public static boolean isAuthenticated() {
        return usuarioActual != null;
    }

    public static User getCurrentUser() {
        return usuarioActual;
    }

    public static boolean hasRole(String rol) {
        return usuarioActual != null && usuarioActual.getRole().equals(rol);
    }

    public static List<User> getAllUsers() throws SQLException {
        List<User> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY user_id";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                User usuario = new User();
                usuario.setUserId(rs.getInt("user_id"));
                usuario.setUsername(rs.getString("username"));
                usuario.setEmail(rs.getString("email"));
                usuario.setRole(rs.getString("role"));
                usuarios.add(usuario);
            }
        }
        return usuarios;
    }
} 