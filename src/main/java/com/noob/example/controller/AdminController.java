package com.noob.example.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noob.example.dto.BookACarDto;
import com.noob.example.dto.CarDto;
import com.noob.example.dto.CarDtoListDto;
import com.noob.example.dto.SearchCarDto;
import com.noob.example.services.admin.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

    @RestController
    @RequestMapping("/api/admin")
    @RequiredArgsConstructor
    public class AdminController {

    private final AdminService adminService;

    @PostMapping("/car")
    public ResponseEntity<?> postCar(
            @RequestPart("notes") String notesJson,
            @ModelAttribute CarDto carDto
    ) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        carDto.setNotes(objectMapper.readValue(notesJson, new TypeReference<List<String>>() {}));
        boolean success = adminService.postCar(carDto);
        return success ? ResponseEntity.status(HttpStatus.CREATED).build() : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping("/cars")
    public ResponseEntity<?> getAllCars() {

        return ResponseEntity.ok(adminService.getAllCars());
    }

    @DeleteMapping("/car/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        adminService.deleteCar(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/car/{id}")
    public ResponseEntity<CarDto> getCarById(@PathVariable Long id) {
       CarDto carDto = adminService.getCarById(id);
       return ResponseEntity.ok(carDto);
    }

    @PutMapping("car/{carId}")
    public ResponseEntity<Void> updateCar(@PathVariable Long carId, @ModelAttribute CarDto carDto) throws IOException {
        try {
            boolean success = adminService.updateCar(carId, carDto);
            if (success) return ResponseEntity.status(HttpStatus.OK).build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/car/bookings")
    public ResponseEntity<List<BookACarDto>> getBookings() {
        return ResponseEntity.ok(adminService.getBookings());
    }

    @GetMapping("/car/booking/{bookingId}/{status}")
    public ResponseEntity<?> changeBookingStatus(@PathVariable Long bookingId, @PathVariable String status) {
       boolean success = adminService.changeBookingStatus(bookingId, status);
       if (success) return ResponseEntity.ok().build();
       return ResponseEntity.notFound().build();
    }



    @PostMapping("/car/search")
    public ResponseEntity<?> searchCar(@RequestBody SearchCarDto searchCarDto) {
        try {
            // Gọi service để tìm kiếm các xe dựa trên điều kiện
            CarDtoListDto result = adminService.searchCar(searchCarDto);
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

