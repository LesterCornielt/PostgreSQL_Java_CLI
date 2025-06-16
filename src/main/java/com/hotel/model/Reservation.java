package com.hotel.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Reservation {
    private Integer reservationId;
    private Integer clientId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfGuests;
    private BigDecimal totalPrice;
    private String status;
    private LocalDateTime createdAt;
    private List<ReservationDetail> details;
} 