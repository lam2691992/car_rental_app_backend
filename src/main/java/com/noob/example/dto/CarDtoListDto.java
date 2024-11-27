package com.noob.example.dto;

import lombok.Data;

import java.util.List;

@Data
public class CarDtoListDto {

    private List<CarDto> carDtoList;
    private String message;

}
