package com.hotel.model;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class Habitacion {
    private String numHabitacion;
    private Integer piso;
    private String estado;
    private Boolean disponible;
    private Integer idTipo;
    private TipoHabitacion tipoHabitacion;
    private String descripcion;
    private Integer capacidad;
    private BigDecimal precioPorNoche;
    private List<CaracteristicaHabitacion> caracteristicas;

    public String getTipoHabitacion() {
        return tipoHabitacion != null ? tipoHabitacion.getNombre() : "";
    }

    public void setTipoHabitacion(String nombre) {
        if (tipoHabitacion == null) {
            tipoHabitacion = new TipoHabitacion();
        }
        tipoHabitacion.setNombre(nombre);
    }
} 