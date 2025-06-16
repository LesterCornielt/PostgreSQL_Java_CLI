package com.hotel.model;

import lombok.Data;

@Data
public class Empleado {
    private Integer idEmpleado;
    private String nombre;
    private String cargo;
    private Integer supervisorId;
    private String email;
    private String password;
} 