package com.noob.example.services.customer;

import com.noob.example.dto.BookACarDto;
import com.noob.example.dto.CarDto;
import com.noob.example.dto.CarDtoListDto;
import com.noob.example.dto.SearchCarDto;

import java.util.List;

public interface CustomerService {

    List<CarDto> getAllCars();

    boolean bookACar(BookACarDto bookACarDto);

    CarDto getCarById(Long carId);

    List<BookACarDto> getBookingsByUserId(Long userId);

    CarDtoListDto searchCar(SearchCarDto searchCarDto);
}
