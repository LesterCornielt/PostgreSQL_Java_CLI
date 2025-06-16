package com.hotel.model;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ReservationDetail {
    private Integer reservationId;
    private Integer roomId;
    private BigDecimal pricePerNight;
} 