package com.hotel.model;

import lombok.Data;

@Data
public class Cliente {
    private Integer idCliente;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String direccion;
} 