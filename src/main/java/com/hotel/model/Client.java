package com.hotel.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Client {
    private Integer clientId;
    private Integer userId;
    private String firstName;
    private String lastName;
    private String identification;
    private String country;
    private String gender;
    private LocalDate birthDate;
    private Integer age;
} 