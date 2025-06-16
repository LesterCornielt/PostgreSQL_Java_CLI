package com.hotel.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Integer userId;
    private String username;
    private String password;
    private String email;
    private String role;
    private LocalDateTime createdAt;
} 