package com.noob.example.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.util.Date;
import java.util.List;

@Data
public class CarDto {

    private Long id;
    private String brand;
    private String color;
    private String name;
    private String type;
    private String transmission;
    private String description;
    private Long price;
    private Date year;
    private  MultipartFile image;
    private byte[] returnedImage;
    private Long bookCarStatus;
    private List<String> notes;
}
