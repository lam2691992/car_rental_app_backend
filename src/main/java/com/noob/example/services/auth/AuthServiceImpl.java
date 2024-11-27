package com.noob.example.services.auth;


import com.noob.example.dto.SignupRequest;
import com.noob.example.dto.UserDto;
import com.noob.example.entity.User;
import com.noob.example.enums.UserRole;
import com.noob.example.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {


    private final UserRepository userRepository;

    @PostConstruct
    // tạo tk admin
    public void createAdminAccount() {
        User adminAccount = userRepository.findByUserRole(UserRole.ADMIN);
        if (adminAccount == null) {
            User newAdminAccount1 = new User();
            newAdminAccount1.setName("Admin");
            newAdminAccount1.setEmail("lam269.lnv@gmail.com");
            newAdminAccount1.setPassword(new BCryptPasswordEncoder().encode("Lam26091992"));
            newAdminAccount1.setUserRole(UserRole.ADMIN);
            userRepository.save(newAdminAccount1);
            System.out.println("Admin Account Created Successfully");
        }
    }

    @Override
    public UserDto createCustomer(SignupRequest signupRequest) {
        // Khởi tạo BCryptPasswordEncoder
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // init model
        User user = new User(signupRequest);

        // Mã hóa mật khẩu và luu vào đối tượng User
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        // save vao db
        userRepository.save(user);

        // convert dto

        return new UserDto(user);
    }

    @Override
    public boolean hasCustomerWithEmail(String email) {
        return userRepository.findFirstByEmail(email).isPresent();
    }
}