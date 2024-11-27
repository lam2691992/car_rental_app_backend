package com.noob.example.dto;


import com.noob.example.enums.UserRole;
import lombok.Data;

@Data
public class AuthenticationResponse {

    public String jwt;
    private UserRole userRole;
    private Long userId;
}
