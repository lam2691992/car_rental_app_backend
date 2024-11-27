package com.noob.example.dto;

import lombok.Data;

@Data
public class SearchCarDto {

    private String brand;
    private String type;
    private String transmission;
    private String color;
    private String email;
    private Long minPrice;
    private Long maxPrice;
}
