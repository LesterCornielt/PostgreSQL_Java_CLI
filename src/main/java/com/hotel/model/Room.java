package com.hotel.model;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class Room {
    private Integer roomId;
    private String roomNumber;
    private String roomType;
    private Integer capacity;
    private BigDecimal pricePerNight;
    private String status;
    private String description;
    private List<RoomFeature> features;
} 