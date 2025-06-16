package com.hotel.service;

import com.hotel.model.Cliente;
import com.hotel.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServicioCliente {
    
    public void agregarCliente(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente (primer_nombre, segundo_nombre, sexo, fecha_nacimiento, tipo_cliente) " +
                    "VALUES (?, ?, ?, ?, ?) RETURNING id_cliente";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getApellido());
            stmt.setString(3, "O"); // Por defecto
            stmt.setDate(4, java.sql.Date.valueOf(java.time.LocalDate.now())); // Por defecto
            stmt.setString(5, "REGULAR"); // Por defecto
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int idCliente = rs.getInt("id_cliente");
                    cliente.setIdCliente(idCliente);
                    
                    // Insertar email
                    if (cliente.getEmail() != null && !cliente.getEmail().isEmpty()) {
                        insertarEmail(idCliente, cliente.getEmail());
                    }
                    
                    // Insertar teléfono
                    if (cliente.getTelefono() != null && !cliente.getTelefono().isEmpty()) {
                        insertarTelefono(idCliente, cliente.getTelefono());
                    }
                }
            }
        }
    }
    
    private void insertarEmail(int idCliente, String email) throws SQLException {
        String sql = "INSERT INTO email_cliente (id_cliente, email) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            stmt.setString(2, email);
            stmt.executeUpdate();
        }
    }
    
    private void insertarTelefono(int idCliente, String telefono) throws SQLException {
        String sql = "INSERT INTO telefono_cliente (id_cliente, telefono) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            stmt.setString(2, telefono);
            stmt.executeUpdate();
        }
    }

    public Cliente obtenerClientePorId(int idCliente) throws SQLException {
        String sql = "SELECT c.*, e.email, t.telefono " +
                    "FROM cliente c " +
                    "LEFT JOIN email_cliente e ON c.id_cliente = e.id_cliente " +
                    "LEFT JOIN telefono_cliente t ON c.id_cliente = t.id_cliente " +
                    "WHERE c.id_cliente = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setIdCliente(rs.getInt("id_cliente"));
                    cliente.setNombre(rs.getString("primer_nombre"));
                    cliente.setApellido(rs.getString("segundo_nombre"));
                    cliente.setEmail(rs.getString("email"));
                    cliente.setTelefono(rs.getString("telefono"));
                    return cliente;
                }
            }
        }
        return null;
    }

    public void actualizarCliente(Cliente cliente) throws SQLException {
        String sql = "UPDATE cliente SET primer_nombre = ?, segundo_nombre = ? WHERE id_cliente = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getApellido());
            stmt.setInt(3, cliente.getIdCliente());
            stmt.executeUpdate();
            
            // Actualizar email si ha cambiado
            if (cliente.getEmail() != null && !cliente.getEmail().isEmpty()) {
                actualizarEmail(cliente.getIdCliente(), cliente.getEmail());
            }
            
            // Actualizar teléfono si ha cambiado
            if (cliente.getTelefono() != null && !cliente.getTelefono().isEmpty()) {
                actualizarTelefono(cliente.getIdCliente(), cliente.getTelefono());
            }
        }
    }
    
    private void actualizarEmail(int idCliente, String email) throws SQLException {
        String sql = "UPDATE email_cliente SET email = ? WHERE id_cliente = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setInt(2, idCliente);
            stmt.executeUpdate();
        }
    }
    
    private void actualizarTelefono(int idCliente, String telefono) throws SQLException {
        String sql = "UPDATE telefono_cliente SET telefono = ? WHERE id_cliente = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, telefono);
            stmt.setInt(2, idCliente);
            stmt.executeUpdate();
        }
    }

    public void eliminarCliente(int idCliente) throws SQLException {
        // Primero eliminar registros relacionados
        String sqlEmail = "DELETE FROM email_cliente WHERE id_cliente = ?";
        String sqlTelefono = "DELETE FROM telefono_cliente WHERE id_cliente = ?";
        String sqlCliente = "DELETE FROM cliente WHERE id_cliente = ?";
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            // Eliminar email
            try (PreparedStatement stmt = conn.prepareStatement(sqlEmail)) {
                stmt.setInt(1, idCliente);
                stmt.executeUpdate();
            }
            
            // Eliminar teléfono
            try (PreparedStatement stmt = conn.prepareStatement(sqlTelefono)) {
                stmt.setInt(1, idCliente);
                stmt.executeUpdate();
            }
            
            // Finalmente eliminar el cliente
            try (PreparedStatement stmt = conn.prepareStatement(sqlCliente)) {
                stmt.setInt(1, idCliente);
                stmt.executeUpdate();
            }
        }
    }
    
    public List<Cliente> obtenerTodosLosClientes() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT c.*, e.email, t.telefono " +
                    "FROM cliente c " +
                    "LEFT JOIN email_cliente e ON c.id_cliente = e.id_cliente " +
                    "LEFT JOIN telefono_cliente t ON c.id_cliente = t.id_cliente " +
                    "ORDER BY c.id_cliente";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setIdCliente(rs.getInt("id_cliente"));
                cliente.setNombre(rs.getString("primer_nombre"));
                cliente.setApellido(rs.getString("segundo_nombre"));
                cliente.setEmail(rs.getString("email"));
                cliente.setTelefono(rs.getString("telefono"));
                clientes.add(cliente);
            }
        }
        return clientes;
    }
} 