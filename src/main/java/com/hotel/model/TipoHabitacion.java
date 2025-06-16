package com.hotel.model;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class TipoHabitacion {
    private Integer idTipo;
    private String nombre;
    private String descripcion;
    private List<String> servicios;
    private Integer capacidad;
    private BigDecimal precioPorNoche;
} 