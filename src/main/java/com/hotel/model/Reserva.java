package com.hotel.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class Reserva {
    private Integer idReserva;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal totalEstadia;
    private Integer idCliente;
    private Integer idEmpleado;
    private List<DetalleReserva> detalles;
    private Integer numHuespedes;
    private String estado;

    public Reserva() {
        this.detalles = new ArrayList<>();
    }

    public List<DetalleReserva> getDetalles() {
        if (detalles == null) {
            detalles = new ArrayList<>();
        }
        return detalles;
    }
} 