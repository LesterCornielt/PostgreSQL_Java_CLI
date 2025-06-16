package com.hotel.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DetalleReserva {
    private Integer idReserva;
    private String numHabitacion;
    private LocalDate fechaEspecifica;
    private String estado;
    private BigDecimal precioPorNoche;
} 