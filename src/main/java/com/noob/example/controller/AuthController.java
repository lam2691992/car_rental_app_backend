package com.noob.example.controller;

import com.noob.example.dto.AuthenticationRequest;
import com.noob.example.dto.AuthenticationResponse;
import com.noob.example.dto.SignupRequest;
import com.noob.example.dto.UserDto;
import com.noob.example.entity.User;
import com.noob.example.repository.UserRepository;
import com.noob.example.response.BaseResponse;
import com.noob.example.services.auth.AuthService;
import com.noob.example.services.jwt.UserService;
import com.noob.example.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private  UserService userService;

    private final JwtUtil jwtUtil;

    @Autowired
    private  UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<BaseResponse> signupCustomer(@RequestBody SignupRequest signupRequest) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setErrorCode("0000");
        baseResponse.setErrorMess("Create user successfully");

        try {
            // Kiểm tra xem có khách hàng nào đã tồn tại với email này không
            if (authService.hasCustomerWithEmail(signupRequest.getEmail())) {
                baseResponse.setErrorCode("0001");
                baseResponse.setErrorMess("User with this email already exist!");
                return new ResponseEntity<>(baseResponse, HttpStatus.NOT_ACCEPTABLE);
            }
            UserDto createCustomerDto = authService.createCustomer(signupRequest);

            if (createCustomerDto != null) {
                baseResponse.setData(createCustomerDto);

            } else {
                baseResponse.setErrorCode("0002");
                baseResponse.setErrorMess("Unable to create");
            }

        } catch (Exception e) {
            logger.error("Error creating user: ", e);
            baseResponse.setErrorCode("0003");
            baseResponse.setErrorMess("Unable to create");
        }

        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @GetMapping("/check-email-exists")
    public ResponseEntity<Object> checkEmail(@RequestParam String email) {
        logger.info("Received request to check email: {}", email);

        // Thêm độ trễ 3 giây
//        try {
//            Thread.sleep(3000); // Tạm dừng 3 giây
//        } catch (InterruptedException e) {
//            logger.error("Thread was interrupted while sleeping", e);
//            Thread.currentThread().interrupt(); // Phục hồi trạng thái gián đoạn
//        }

        // Kiểm tra email tồn tại
        boolean exists = userService.emailExists(email);
        logger.info("Email exists: {}", exists);

        // Tạo phản hồi trả về
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);

        // Trả về kết quả
        return ResponseEntity.ok(response);
    }



    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            // Xác thực người dùng với email và mật khẩu
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getEmail(),
                            authenticationRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            // mã lỗi 401 nếu thông tin đăng nhập sai
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect email or password");
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account is disabled");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }

        // Nếu xác thực thành công, lấy thông tin người dùng và tạo JWT token
        UserDetails userDetails = userService.userDetailsService().loadUserByUsername(authenticationRequest.getEmail());
        Optional<User> optionalUser = userRepository.findFirstByEmail(userDetails.getUsername());

        if (optionalUser.isPresent()) {
            String jwt = jwtUtil.generateToken(userDetails);
            AuthenticationResponse authenticationResponse = new AuthenticationResponse();
            authenticationResponse.setJwt(jwt);
            authenticationResponse.setUserId(optionalUser.get().getId());
            authenticationResponse.setUserRole(optionalUser.get().getUserRole());
            return ResponseEntity.ok(authenticationResponse);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }
}
