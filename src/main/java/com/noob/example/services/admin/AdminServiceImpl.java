package com.noob.example.services.admin;

import com.noob.example.dto.BookACarDto;
import com.noob.example.dto.CarDto;
import com.noob.example.dto.CarDtoListDto;
import com.noob.example.dto.SearchCarDto;
import com.noob.example.entity.BookACar;
import com.noob.example.entity.Car;
import com.noob.example.entity.CarImage;
import com.noob.example.entity.CarNote;
import com.noob.example.enums.BookCarStatus;
import com.noob.example.repository.BookACarRepository;
import com.noob.example.repository.CarNoteRepository;
import com.noob.example.repository.CarRepository;
import com.noob.example.specifications.BookACarSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {


    private final CarRepository carRepository;

    private final BookACarRepository bookACarRepository;

    private final CarNoteRepository carNoteRepository;

    @Override
    public boolean postCar(CarDto carDto) throws IOException {
        try {
            // Tạo đối tượng Car
            Car car = new Car();
            car.setName(carDto.getName());
            car.setBrand(carDto.getBrand());
            car.setColor(carDto.getColor());
            car.setPrice(carDto.getPrice());
            car.setYear(carDto.getYear());
            car.setType(carDto.getType());
            car.setDescription(carDto.getDescription());
            car.setTransmission(carDto.getTransmission());

            // Nếu có ảnh trong CarDto, tạo đối tượng CarImage
            if (carDto.getImage() != null) {
                CarImage carImage = new CarImage();
                carImage.setImage(carDto.getImage().getBytes());
                carImage.setCar(car);
                car.setCarImage(carImage);
            }

            // Save Car (Hibernate sẽ tự động lưu CarImage vì cascade)
            carRepository.save(car);

            // Lưu danh sách ghi chú nếu có
            if (carDto.getNotes() != null && !carDto.getNotes().isEmpty()) {
                for (String note : carDto.getNotes()) {
                    CarNote carNote = new CarNote();
                    carNote.setCar(car);
                    carNote.setNote(note != null ? note : "No note"); // Đảm bảo luôn có giá trị cho `noted`
                    carNoteRepository.save(carNote);
                }
            } else {
                // Nếu không có ghi chú, thêm một ghi chú mặc định
                CarNote defaultCarNote = new CarNote();
                defaultCarNote.setCar(car);
                defaultCarNote.setNote("No note"); // Ghi chú mặc định nếu không có
                carNoteRepository.save(defaultCarNote);
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }




    @Override
    public List<CarDto> getAllCars() {
        return carRepository.findAll().stream().map(Car::getCarDto).collect(Collectors.toList());
    }

    @Override
    public CarDto getCarById(Long id) {
        Optional<Car> optionalCar = carRepository.findById(id);
        if (optionalCar.isPresent()) {
            Car car = optionalCar.get();
            CarDto carDto = car.getCarDto();

            if (car.getCarImage() != null) {
                carDto.setReturnedImage(car.getCarImage().getImage());
            }

            // Lấy danh sách ghi chú
            List<String> notes = carNoteRepository.findByCarId(car.getId())
                    .stream()
                    .map(CarNote::getNote)
                    .collect(Collectors.toList());
            carDto.setNotes(notes);

            return carDto;
        }
        return null;
    }



    @Override
    public boolean updateCar(Long carId, CarDto carDto) throws IOException {
        Optional<Car> optionalCar = carRepository.findById(carId);
        if (optionalCar.isPresent()) {
            Car existingCar = optionalCar.get();

            // Cập nhật các thông tin cơ bản của Car
            existingCar.setPrice(carDto.getPrice());
            existingCar.setYear(carDto.getYear());
            existingCar.setType(carDto.getType());
            existingCar.setColor(carDto.getColor());
            existingCar.setDescription(carDto.getDescription());
            existingCar.setTransmission(carDto.getTransmission());
            existingCar.setBrand(carDto.getBrand());
            existingCar.setName(carDto.getName());

            // Cập nhật CarImage nếu có ảnh mới
            if (carDto.getImage() != null) {
                if (existingCar.getCarImage() != null) {
                    existingCar.getCarImage().setImage(carDto.getImage().getBytes());
                } else {
                    CarImage carImage = new CarImage();
                    carImage.setImage(carDto.getImage().getBytes());
                    carImage.setCar(existingCar);
                    existingCar.setCarImage(carImage);
                }
            }

            carRepository.save(existingCar);
            return true;
        } else {
            return false;
        }
    }


    @Override
    public List<BookACarDto> getBookings() {
        return bookACarRepository.findAll().stream().map(BookACar::getBookACarDto).collect(Collectors.toList());
    }

    @Override
    public boolean changeBookingStatus(Long bookingID, String status) {
        Optional<BookACar> optionalBookACar = bookACarRepository.findById(bookingID);
        if (optionalBookACar.isPresent()) {
            BookACar existingBookACar = optionalBookACar.get();
            if (status.equals("1")) // Kiểm tra với giá trị "1" cho Approved
                existingBookACar.setBookCarStatus(BookCarStatus.APPROVED);
            else if (status.equals("2")) // Kiểm tra với giá trị "2" cho Rejected
                existingBookACar.setBookCarStatus(BookCarStatus.REJECTED);
            bookACarRepository.save(existingBookACar);
            return true;
        }
        return false;
    }

    @Override
    public CarDtoListDto searchCar(SearchCarDto searchCarDto) {
        // Sử dụng ExampleMatcher để tìm kiếm dựa trên các thuộc tính của Car
        Car car = new Car();
        if (searchCarDto.getBrand() != null) car.setBrand(searchCarDto.getBrand());
        if (searchCarDto.getType() != null) car.setType(searchCarDto.getType());
        if (searchCarDto.getTransmission() != null) car.setTransmission(searchCarDto.getTransmission());
        if (searchCarDto.getColor() != null) car.setColor(searchCarDto.getColor());


        ExampleMatcher exampleMatcher = ExampleMatcher.matchingAll()
                .withMatcher("brand", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("type", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("transmission", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("color", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Example<Car> carExample = Example.of(car, exampleMatcher);

        // Sử dụng Specification để tìm kiếm dựa trên email và khoảng giá
        Specification<BookACar> spec = Specification.where(
                BookACarSpecifications.hasEmail(searchCarDto.getEmail())
        );

        // Nếu có giá trị minPrice và maxPrice, áp dụng thêm điều kiện giữa khoảng giá
        if (searchCarDto.getMinPrice() != null && searchCarDto.getMaxPrice() != null) {
            spec = spec.and(BookACarSpecifications.betweenPrice(searchCarDto.getMinPrice(), searchCarDto.getMaxPrice()));
        }


        // Tạo đối tượng Sort với trường fromDate
        Sort sort = Sort.by(Sort.Order.asc("fromDate"));

        // Tìm kiếm bookingList với Specification và Sort
        List<BookACar> bookingList = bookACarRepository.findAll(spec, sort);

        // Tìm tất cả xe theo Example
        List<Car> carList = carRepository.findAll(carExample);

        // Lọc những xe có id trùng với kết quả tìm kiếm theo email và khoảng giá
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

    @Override
    public void deleteCar(Long id) {
        carRepository.deleteById(id); // Xóa xe khỏi cơ sở dữ liệu dựa trên ID
    }
}