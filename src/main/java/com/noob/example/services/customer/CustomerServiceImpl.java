package com.noob.example.services.customer;

import com.noob.example.dto.*;
import com.noob.example.entity.BookACar;
import com.noob.example.entity.Car;
import com.noob.example.entity.User;
import com.noob.example.enums.BookCarStatus;
import com.noob.example.repository.BookACarRepository;
import com.noob.example.repository.CarRepository;
import com.noob.example.repository.UserRepository;
import com.noob.example.security.model.UserDetailImpl;
import com.noob.example.specifications.BookACarSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CarRepository carRepository;

    private final UserRepository userRepository;

    private final BookACarRepository bookACarRepository;

    @Override
    public List<CarDto> getAllCars() {
        return carRepository.findAll().stream().map(car -> {
            CarDto carDto = car.getCarDto();

            // Kiểm tra trạng thái booking
            boolean isApproved = bookACarRepository.existsByCarIdAndBookCarStatus(car.getId(), BookCarStatus.APPROVED);

            // Set trạng thái bookCarStatus
            carDto.setBookCarStatus(isApproved ? 1L : 0L); // 1 = Đã được book, 0 = Chưa được book

            return carDto;
        }).collect(Collectors.toList());
    }


    @Override
    public boolean bookACar(BookACarDto bookACarDto) {
        // Kiểm tra ID của xe và người dùng
        if (bookACarDto.getCarId() == null) {
            throw new IllegalArgumentException("Car ID không được phép null");
        }
        User userDetailsImpl = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetailsImpl == null || userDetailsImpl.getId() == null) {
            throw new IllegalArgumentException("User ID không được phép null");
        }

        Car existingCar  = carRepository.findById(bookACarDto.getCarId()).orElse(null);
        User userExist = userRepository.findById(userDetailsImpl.getId()).orElse(null);

        if (existingCar != null && userExist != null) {
            BookACar bookACar = getBookACar(bookACarDto, userExist, existingCar);
            bookACarRepository.save(bookACar);
            return true;
        }
        return false;
    }


    private static BookACar getBookACar(BookACarDto bookACarDto, User userExist, Car existingCar) {
        BookACar bookACar = new BookACar();
        bookACar.setUser(userExist);
        bookACar.setCar(existingCar);
        bookACar.setFromDate(bookACarDto.getFromDate());
        bookACar.setToDate(bookACarDto.getToDate());
        bookACar.setBookCarStatus(BookCarStatus.PENDING);

        // Lấy ngày bắt đầu và ngày kết thúc
        Date fromDate = bookACarDto.getFromDate();
        Date toDate = bookACarDto.getToDate();

        // Đảm bảo toDate là sau fromDate
        if (toDate.before(fromDate)) {
            throw new IllegalArgumentException("To Date must be after From Date.");
        }

        // Tính số ngày
        long diffInMilliseconds = toDate.getTime() - fromDate.getTime();
        long days = TimeUnit.MILLISECONDS.toDays(diffInMilliseconds) + 1; // Cộng thêm 1 để tính cả ngày bắt đầu

        // Đảm bảo ít nhất 1 ngày nếu có đặt xe
        if (days < 1) {
            days = 1;
        }

        bookACar.setDays(days);
        bookACar.setPrice(existingCar.getPrice() * days);

        return bookACar;
    }

    @Override
    public CarDto getCarById(Long carId) {
        Optional<Car> optionalCar = carRepository.findById(carId);
        return optionalCar.map(Car::getCarDto).orElse(null);
    }

    @Override
    public List<BookACarDto> getBookingsByUserId(Long userId) {
        return bookACarRepository.findAllByUserId(userId)
                .stream()
                .map(BookACar::getBookACarDto) // Sử dụng lambda
                .collect(Collectors.toList());
    }

    @Override
    public CarDtoListDto searchCar(SearchCarDto searchCarDto) {
        // Sử dụng ExampleMatcher để tìm kiếm dựa trên các thuộc tính của Car
        Car car = new Car();
        car.setBrand(searchCarDto.getBrand());
        car.setType(searchCarDto.getType());
        car.setTransmission(searchCarDto.getTransmission());
        car.setColor(searchCarDto.getColor());

        ExampleMatcher exampleMatcher = ExampleMatcher.matchingAll()
                .withMatcher("brand", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("type", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("transmission", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("color", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Example<Car> carExample = Example.of(car, exampleMatcher);

        // Sử dụng Specification để tìm kiếm dựa trên email
        Specification<BookACar> spec = Specification.where(
                BookACarSpecifications.hasEmail(searchCarDto.getEmail())
        );

        // Tạo đối tượng Sort với trường thực tế
        Sort sort = Sort.by(Sort.Order.asc("fromDate")); // Sắp xếp theo fromDate

        // Tìm kiếm bookingList với Specification và Sort
        List<BookACar> bookingList = bookACarRepository.findAll(spec, sort);

        // Tìm tất cả xe theo Example
        List<Car> carList = carRepository.findAll(carExample);

        // Lọc những xe có `id` trùng với kết quả tìm kiếm theo `email`
        List<Car> filteredCarList = carList.stream()
                .filter(c -> bookingList.stream()
                        .anyMatch(b -> b.getCar().getId().equals(c.getId())))
                .toList();

        // Đổi kết quả thành DTO
        CarDtoListDto carDtoListDto = new CarDtoListDto();
        carDtoListDto.setCarDtoList(filteredCarList.stream()
                .map(Car::getCarDto)
                .collect(Collectors.toList()));
        return carDtoListDto;
    }
}
