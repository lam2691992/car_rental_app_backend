package com.noob.example.entity;

import com.noob.example.dto.CarDto;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Data
@Table(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String brand;
    private String color;
    private String name;
    private String type;
    private String transmission;
    private String description;
    private Long price;
    private Date year;
    private Long bookCarStatus;

    // Mối quan hệ One-to-One với bảng `car_images`
    @OneToOne(mappedBy = "car", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CarImage carImage;
    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CarNote> notes = new ArrayList<>();


    public CarDto getCarDto() {
        CarDto carDto = new CarDto();
        carDto.setId(id);
        carDto.setName(name);
        carDto.setBrand(brand);
        carDto.setColor(color);
        carDto.setPrice(price);
        carDto.setDescription(description);
        carDto.setType(type);
        carDto.setTransmission(transmission);
        carDto.setYear(year);

        // Lấy ảnh nếu tồn tại
        if (carImage != null) {
            carDto.setReturnedImage(carImage.getImage());
        }

        // Lấy ghi chú
        if (notes != null) {
            carDto.setNotes(notes.stream().map(CarNote::getNoted).toList());

        }
        return carDto;
    }
}
