package com.hotel.model;

import lombok.Data;

@Data
public class RoomFeature {
    private Integer featureId;
    private Integer roomId;
    private String featureName;
    private String featureValue;
} 