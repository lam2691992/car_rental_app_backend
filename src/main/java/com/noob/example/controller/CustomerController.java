package com.noob.example.controller;


import com.noob.example.dto.BookACarDto;
import com.noob.example.dto.CarDto;
import com.noob.example.dto.CarDtoListDto;
import com.noob.example.dto.SearchCarDto;
import com.noob.example.services.customer.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping("/cars")
    public ResponseEntity<List<CarDto>> getALlCars() {
        List<CarDto> carDtoList = customerService.getAllCars();
        return ResponseEntity.ok(carDtoList);
    }

    @PostMapping("/car/book")
    public ResponseEntity<Void> bookACar(@RequestBody BookACarDto bookACarDto) {
        System.out.println("Received BookACarDto: " + bookACarDto);
        boolean success = customerService.bookACar(bookACarDto);
        if (success) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    

    @GetMapping("/car/{carId}")
    public ResponseEntity<CarDto> getCarById (@PathVariable Long carId) {
        CarDto carDto = customerService.getCarById(carId);
        if (carDto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(carDto);
    }

    @GetMapping("/car/bookings/{userId}")
    public ResponseEntity<List<BookACarDto>> getBookingsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(customerService.getBookingsByUserId(userId));
    }

    @PostMapping("/car/search")
    public ResponseEntity<?> searchCar(@RequestBody SearchCarDto searchCarDto) {
        try {
            // Gọi service để tìm kiếm các xe dựa trên điều kiện
            CarDtoListDto result = customerService.searchCar(searchCarDto);

            // Kiểm tra nếu danh sách kết quả trống
            if (result.getCarDtoList().isEmpty()) {
                result.setMessage("No cars found matching the search criteria.");
                return ResponseEntity.ok(result);
            }
            // Trả về danh sách xe tìm được
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // Xử lý ngoại lệ nếu có lỗi xảy ra
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while searching for cars.");
        }
    }

    @GetMapping("/cars/brands")
    public ResponseEntity<List<String>> getCarBrands() {
        List<String> brands = Arrays.asList("BMW", "AUDI", "TOYOTA", "HONDA", "MAZDA", "KIA", "LEXUS", "VINFAST");
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/cars/types")
    public ResponseEntity<List<String>> getCarTypes() {
        List<String> types = Arrays.asList("Petrol", "Hybrid", "Electric", "Diesel");
        return ResponseEntity.ok(types);
    }

    @GetMapping("/cars/colors")
    public ResponseEntity<List<String>> getCarColors() {
        List<String> colors = Arrays.asList("Red", "White", "Blue", "Black", "Grey", "Silver");
        return ResponseEntity.ok(colors);
    }

    @GetMapping("/cars/transmissions")
    public ResponseEntity<List<String>> getCarTransmissions() {
        List<String> transmissions = Arrays.asList("Manual", "Automatic");
        return ResponseEntity.ok(transmissions);
    }
}
