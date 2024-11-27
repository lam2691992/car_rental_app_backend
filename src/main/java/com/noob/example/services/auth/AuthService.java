package com.noob.example.services.auth;

import com.noob.example.dto.SignupRequest;
import com.noob.example.dto.UserDto;

public interface AuthService {
 UserDto createCustomer(SignupRequest signupRequest);

 boolean hasCustomerWithEmail(String email);
}
