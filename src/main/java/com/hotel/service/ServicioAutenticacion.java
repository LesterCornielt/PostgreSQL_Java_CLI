package com.hotel.service;

import com.hotel.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import com.hotel.model.Empleado;

public class ServicioAutenticacion {
    private static Empleado empleadoActual = null;

    public static Map<String, Object> login(String email, String password) throws SQLException {
        Map<String, Object> resultado = new HashMap<>();
        
        String sql = "SELECT id_empleado, nombre, cargo, email " +
                    "FROM empleado " +
                    "WHERE email = ? AND password = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Empleado empleado = new Empleado();
                    empleado.setIdEmpleado(rs.getInt("id_empleado"));
                    empleado.setNombre(rs.getString("nombre"));
                    empleado.setCargo(rs.getString("cargo"));
                    empleado.setEmail(rs.getString("email"));
                    
                    empleadoActual = empleado;
                    resultado.put("success", true);
                    resultado.put("empleado", empleado);
                } else {
                    resultado.put("success", false);
                    resultado.put("message", "Credenciales inválidas");
                }
            }
        }
        
        return resultado;
    }

    public static void logout() {
        empleadoActual = null;
    }

    public static boolean isAuthenticated() {
        return empleadoActual != null;
    }

    public static Empleado getCurrentUser() {
        return empleadoActual;
    }

    public static boolean hasRole(String cargo) {
        return empleadoActual != null && empleadoActual.getCargo().equals(cargo);
    }

    public static List<Empleado> getAllEmpleados() throws SQLException {
        List<Empleado> empleados = new ArrayList<>();
        String sql = "SELECT id_empleado, nombre, cargo, email FROM empleado ORDER BY id_empleado";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Empleado empleado = new Empleado();
                empleado.setIdEmpleado(rs.getInt("id_empleado"));
                empleado.setNombre(rs.getString("nombre"));
                empleado.setCargo(rs.getString("cargo"));
                empleado.setEmail(rs.getString("email"));
                empleados.add(empleado);
            }
        }
        return empleados;
    }
    
    public static boolean cambiarPassword(int idEmpleado, String passwordActual, String nuevoPassword) throws SQLException {
        String sql = "UPDATE empleado SET password = ? " +
                    "WHERE id_empleado = ? AND password = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nuevoPassword);
            stmt.setInt(2, idEmpleado);
            stmt.setString(3, passwordActual);
            
            int filasActualizadas = stmt.executeUpdate();
            return filasActualizadas > 0;
        }
    }
    
    public static boolean restablecerPassword(String email) throws SQLException {
        String sql = "UPDATE empleado SET password = 'password123' " +
                    "WHERE email = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            int filasActualizadas = stmt.executeUpdate();
            return filasActualizadas > 0;
        }
    }
    
    public static boolean verificarPermiso(int idEmpleado, String permisoRequerido) throws SQLException {
        String sql = "SELECT cargo FROM empleado WHERE id_empleado = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idEmpleado);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String cargo = rs.getString("cargo");
                    
                    // Verificar permisos basados en el cargo
                    switch (cargo) {
                        case "GERENTE":
                            return true; // Los gerentes tienen todos los permisos
                        case "RECEPCIONISTA":
                            return permisoRequerido.equals("GESTIONAR_RESERVAS") ||
                                   permisoRequerido.equals("GESTIONAR_CLIENTES");
                        case "LIMPIEZA":
                            return permisoRequerido.equals("GESTIONAR_HABITACIONES");
                        default:
                            return false;
                    }
                }
            }
        }
        
        return false;
    }
} 